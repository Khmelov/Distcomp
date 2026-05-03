using System.Security.Claims;
using Microsoft.AspNetCore.Mvc;
using Publisher.Dtos;
using Publisher.Entities;
using Publisher.Services;

namespace Publisher.Controllers
{
    [ApiController]
    [Route("api/v2.0/users")]
    public class UsersV2Controller : BaseApiController
    {
        private readonly IService<User, UserRequestTo, UserResponseTo> _userService;
        private readonly IAuthService _authService;

        public UsersV2Controller(
            IService<User, UserRequestTo, UserResponseTo> userService,
            IAuthService authService)
        {
            _userService = userService;
            _authService = authService;
        }

        private async Task<ClaimsPrincipal?> GetPrincipalOrFail()
        {
            var authHeader = Request.Headers["Authorization"].ToString();
            var principal = await _authService.VerifyTokenAsync(authHeader);
            if (principal == null)
                throw new UnauthorizedAccessException();
            return principal;
        }

        private async Task<User?> GetCurrentUserOrFail()
        {
            var authHeader = Request.Headers["Authorization"].ToString();
            var user = await _authService.GetUserFromTokenAsync(authHeader);
            return user;
        }

        [HttpGet("{id:long}")]
        public async Task<IActionResult> GetById(long id)
        {
            ClaimsPrincipal principal;
            try
            {
                principal = await GetPrincipalOrFail();
            }
            catch
            {
                return Error("Invalid or missing token", 401, 1);
            }

            var currentUser = await GetCurrentUserOrFail();
            if (currentUser == null)
                return Error("User not found", 401, 2);

            if (!_authService.CanRead(principal, id, currentUser.Id))
                return Error("Access denied", 403, 1);

            var user = await _userService.GetByIdAsync(id);
            if (user == null)
                return Error($"User with id {id} not found", 404, 1);

            return Ok(user);
        }

        [HttpGet]
        public async Task<IActionResult> GetAll()
        {
            ClaimsPrincipal principal;
            try
            {
                principal = await GetPrincipalOrFail();
            }
            catch
            {
                return Error("Invalid or missing token", 401, 1);
            }

            var role = principal.FindFirst("role")?.Value;
            var allUsers = await _userService.GetAllAsync();
            return Ok(allUsers);
            
        }

       

        [HttpPut]
        public async Task<IActionResult> Update([FromBody] UserRequestTo request)
        {
            ClaimsPrincipal principal;
            try
            {
                principal = await GetPrincipalOrFail();
            }
            catch
            {
                return Error("Invalid or missing token", 401, 1);
            }

            var currentUser = await GetCurrentUserOrFail();
            if (currentUser == null)
                return Error("User not found", 401, 2);

            if (!_authService.CanModify(principal, request.Id, currentUser.Id))
                return Error("Access denied", 403, 1);

            try
            {
                var updated = await _userService.UpdateAsync(request.Id, request);
                return Ok(updated);
            }
            catch (KeyNotFoundException)
            {
                return Error($"User with id {request.Id} not found", 404, 1);
            }
            catch (InvalidOperationException ex)
            {
                return Error(ex.Message, 409, 1);
            }
            catch
            {
                return Error("Internal server error", 500, 1);
            }
        }

        [HttpDelete("{id:long}")]
        public async Task<IActionResult> Delete(long id)
        {
            ClaimsPrincipal principal;
            try
            {
                principal = await GetPrincipalOrFail();
            }
            catch
            {
                return Error("Invalid or missing token", 401, 1);
            }

            var currentUser = await GetCurrentUserOrFail();
            if (currentUser == null)
                return Error("User not found", 401, 2);

            if (currentUser.Role != "ADMIN" && currentUser.Id != id)
                return Error("Access denied", 403, 1);

            try
            {
                await _userService.DeleteAsync(id);
                return NoContent();
            }
            catch (KeyNotFoundException)
            {
                return Error($"User with id {id} not found", 404, 1);
            }
            catch
            {
                return Error("Internal server error", 500, 1);
            }
        }
    }
}