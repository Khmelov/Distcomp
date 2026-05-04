using Distcomp.Application.DTOs;
using Distcomp.Application.Interfaces;
using Distcomp.Infrastructure.Caching;
using Microsoft.AspNetCore.Mvc;
using System.Security.Claims;

namespace Distcomp.WebApi.Controllers
{
    [ApiController]
    [Route("api/v1.0/users")]
    [Route("api/v2.0/users")]
    public class UserController : ControllerBase
    {
        private readonly IUserService _userService;
        private readonly RedisCacheService _cache;

        public UserController(IUserService userService, RedisCacheService cache)
        {
            _userService = userService;
            _cache = cache;
        }

        private bool IsV2 => Request.Path.Value?.Contains("/v2.0") ?? false;

        [HttpPost]
        public IActionResult Create([FromBody] UserRequestTo request)
        {
            var response = _userService.Create(request);
            return CreatedAtAction(nameof(GetById), new { id = response.Id }, response);
        }

        [HttpGet("{id:long}")]
        public async Task<IActionResult> GetById(long id)
        {
            if (IsV2 && !User.Identity!.IsAuthenticated)
                return Unauthorized(new { errorMessage = "Unauthorized", errorCode = 40101 });

            string cacheKey = $"user:{id}";

            var cachedUser = await _cache.GetAsync(cacheKey);
            if (!string.IsNullOrEmpty(cachedUser))
            {
                return Content(cachedUser, "application/json");
            }

            var response = _userService.GetById(id);
            if (response == null) return NotFound();

            await _cache.SetAsync(cacheKey, response);

            return Ok(response);
        }

        [HttpGet]
        public IActionResult GetAll()
        {
            if (IsV2 && !User.Identity!.IsAuthenticated)
                return Unauthorized(new { errorMessage = "Unauthorized", errorCode = 40101 });

            return Ok(_userService.GetAll());
        }

        [HttpPut("{id:long?}")]
        public async Task<IActionResult> Update(long id, [FromBody] UserRequestTo request)
        {
            if (IsV2)
            {
                var accessError = CheckSelfAccess(id);
                if (accessError != null) return accessError;
            }

            await _cache.RemoveAsync($"user:{id}");

            var response = _userService.Update(id, request);
            return Ok(response);
        }

        [HttpDelete("{id:long}")]
        public async Task<IActionResult> Delete(long id)
        {
            if (IsV2)
            {
                var accessError = CheckSelfAccess(id);
                if (accessError != null) return accessError;
            }

            await _cache.RemoveAsync($"user:{id}");

            _userService.Delete(id);
            return NoContent();
        }

        private IActionResult? CheckSelfAccess(long userId)
        {
            if (!User.Identity!.IsAuthenticated)
                return Unauthorized(new { errorMessage = "Unauthorized", errorCode = 40101 });

            var role = User.FindFirst("role")?.Value;
            var currentUserLogin = User.FindFirst(ClaimTypes.NameIdentifier)?.Value
                                 ?? User.FindFirst("sub")?.Value;

            if (role == "ADMIN") return null;

            var userToEdit = _userService.GetById(userId);
            if (userToEdit == null) return NotFound();

            if (userToEdit.Login != currentUserLogin)
            {
                return StatusCode(403, new { errorMessage = "Access denied. You can only manage your own profile.", errorCode = 40301 });
            }

            return null;
        }
    }
}