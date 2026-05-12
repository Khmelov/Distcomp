using Distcomp.Application.DTOs;
using Distcomp.Application.Interfaces;
using Microsoft.AspNetCore.Mvc;

namespace Distcomp.WebApi.Controllers
{
    [ApiController]
    [Route("api/v1.0/markers")]
    [Route("api/v2.0/markers")]
    public class MarkerController : ControllerBase
    {
        private readonly IMarkerService _markerService;

        public MarkerController(IMarkerService markerService)
        {
            _markerService = markerService;
        }

        private bool IsV2 => Request.Path.Value?.Contains("/v2.0") ?? false;

        [HttpPost]
        public IActionResult Create([FromBody] MarkerRequestTo request)
        {
            if (IsV2)
            {
                var authError = CheckAdminAccess();
                if (authError != null) return authError;
            }

            var response = _markerService.Create(request);
            return CreatedAtAction(nameof(GetById), new { id = response.Id }, response);
        }

        [HttpGet("{id:long}")]
        public IActionResult GetById(long id)
        {
            if (IsV2 && !User.Identity!.IsAuthenticated)
                return Unauthorized(new { errorMessage = "Unauthorized", errorCode = 40101 });

            var response = _markerService.GetById(id);
            return Ok(response);
        }

        [HttpGet]
        public IActionResult GetAll()
        {
            if (IsV2 && !User.Identity!.IsAuthenticated)
                return Unauthorized(new { errorMessage = "Unauthorized", errorCode = 40101 });

            return Ok(_markerService.GetAll());
        }

        [HttpPut("{id:long?}")]
        public IActionResult Update(long id, [FromBody] MarkerRequestTo request)
        {
            if (IsV2)
            {
                var authError = CheckAdminAccess();
                if (authError != null) return authError;
            }

            var response = _markerService.Update(id, request);
            return Ok(response);
        }

        [HttpDelete("{id:long}")]
        public IActionResult Delete(long id)
        {
            if (IsV2)
            {
                var authError = CheckAdminAccess();
                if (authError != null) return authError;
            }

            _markerService.Delete(id);
            return NoContent();
        }

        private IActionResult? CheckAdminAccess()
        {
            if (!User.Identity!.IsAuthenticated)
                return Unauthorized(new { errorMessage = "Unauthorized", errorCode = 40101 });

            var role = User.FindFirst("role")?.Value;
            if (role != "ADMIN")
            {
                return StatusCode(403, new { errorMessage = "Access denied. Admin role required.", errorCode = 40301 });
            }

            return null;
        }
    }
}