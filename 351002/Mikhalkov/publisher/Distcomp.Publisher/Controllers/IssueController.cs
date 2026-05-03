using Distcomp.Application.DTOs;
using Distcomp.Application.Interfaces;
using Microsoft.AspNetCore.Mvc;
using System.Security.Claims;

namespace Distcomp.WebApi.Controllers
{
    [ApiController]
    [Route("api/v1.0/issues")]
    [Route("api/v2.0/issues")]
    public class IssueController : ControllerBase
    {
        private readonly IIssueService _issueService;
        private readonly IUserService _userService;

        public IssueController(IIssueService issueService, IUserService userService)
        {
            _issueService = issueService;
            _userService = userService;
        }

        private bool IsV2 => Request.Path.Value?.Contains("/v2.0") ?? false;

        [HttpPost]
        public IActionResult Create([FromBody] IssueRequestTo request)
        {
            if (IsV2 && !User.Identity!.IsAuthenticated)
                return Unauthorized(new { errorMessage = "Unauthorized", errorCode = 40101 });

            var response = _issueService.Create(request);
            return CreatedAtAction(nameof(GetById), new { id = response.Id }, response);
        }

        [HttpGet("{id:long}")]
        public IActionResult GetById(long id)
        {
            if (IsV2 && !User.Identity!.IsAuthenticated)
                return Unauthorized(new { errorMessage = "Unauthorized", errorCode = 40101 });

            var response = _issueService.GetById(id);
            return Ok(response);
        }

        [HttpGet]
        public IActionResult GetAll()
        {
            if (IsV2 && !User.Identity!.IsAuthenticated)
                return Unauthorized(new { errorMessage = "Unauthorized", errorCode = 40101 });

            return Ok(_issueService.GetAll());
        }

        [HttpPut("{id:long?}")]
        public IActionResult Update(long id, [FromBody] IssueRequestTo request)
        {
            if (IsV2)
            {
                var securityError = CheckAccess(id);
                if (securityError != null) return securityError;
            }

            return Ok(_issueService.Update(id, request));
        }

        [HttpDelete("{id:long}")]
        public IActionResult Delete(long id)
        {
            if (IsV2)
            {
                var securityError = CheckAccess(id);
                if (securityError != null) return securityError;
            }

            _issueService.Delete(id);
            return NoContent();
        }

        private IActionResult? CheckAccess(long issueId)
        {
            if (!User.Identity!.IsAuthenticated)
                return Unauthorized(new { errorMessage = "Unauthorized", errorCode = 40101 });

            var role = User.FindFirst("role")?.Value;
            var userLogin = User.FindFirst(System.IdentityModel.Tokens.Jwt.JwtRegisteredClaimNames.Sub)?.Value;

            if (role == "ADMIN") return null; 

            var issue = _issueService.GetById(issueId);
            if (issue == null) return NotFound(new { errorMessage = "Issue not found", errorCode = 40402 });

            var owner = _userService.GetById(issue.UserId);
            if (owner?.Login != userLogin)
            {
                return StatusCode(403, new { errorMessage = "Access denied. You are not the owner.", errorCode = 40301 });
            }

            return null;
        }
    }
}