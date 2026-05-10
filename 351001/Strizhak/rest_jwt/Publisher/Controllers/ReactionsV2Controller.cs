using System.Security.Claims;
using Microsoft.AspNetCore.Mvc;
using Publisher.Dtos;
using Publisher.Repositories;
using Publisher.Services;

namespace Publisher.Controllers
{
    [ApiController]
    [Route("api/v2.0/reactions")]
    public class ReactionsV2Controller : BaseApiController
    {
        private readonly KafkaReactionRepository _repository;
        private readonly IAuthService _authService;
        private readonly ILogger<ReactionsV2Controller> _logger;

        public ReactionsV2Controller(
            KafkaReactionRepository repository,
            IAuthService authService,
            ILogger<ReactionsV2Controller> logger)
        {
            _repository = repository;
            _authService = authService;
            _logger = logger;
        }

        private async Task<ClaimsPrincipal?> GetPrincipalOrFail()
        {
            var authHeader = Request.Headers["Authorization"].ToString();
            var principal = await _authService.VerifyTokenAsync(authHeader);
            if (principal == null)
                throw new UnauthorizedAccessException();
            return principal;
        }

        // GET /api/v2.0/reactions/{id} - чтение разрешено всем аутентифицированным
        [HttpGet("{id:long}")]
        public async Task<IActionResult> GetById(long id)
        {
            try
            {
                await GetPrincipalOrFail();
            }
            catch
            {
                return Error("Invalid or missing token", 401, 1);
            }

            var reaction = await _repository.FindByIdAsync(id);
            if (reaction == null)
                return Error($"Reaction with id {id} not found", 404, 1);

            return Ok(reaction);
        }

        // GET /api/v2.0/reactions - чтение разрешено всем аутентифицированным
        [HttpGet]
        public async Task<IActionResult> GetAll()
        {
            try
            {
                await GetPrincipalOrFail();
            }
            catch
            {
                return Error("Invalid or missing token", 401, 1);
            }

            var reactions = await _repository.GetAllAsync();
            return Ok(reactions);
        }

        // POST /api/v2.0/reactions - только ADMIN (так как нет UserId)
        [HttpPost]
        public async Task<IActionResult> Create([FromBody] ReactionRequestTo request)
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
            if (role != "ADMIN")
                return Error("Only ADMIN can create reactions", 403, 1);

            try
            {
                var created = await _repository.CreateAsync(request);
                return CreatedAtAction(nameof(GetById), new { id = created.Id }, created);
            }
            catch (TimeoutException)
            {
                return Error("Discussion service is busy", 504, 1);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error creating reaction");
                return Error("Internal server error", 500, 1);
            }
        }

        // PUT /api/v2.0/reactions - только ADMIN
        [HttpPut]
        public async Task<IActionResult> Update([FromBody] ReactionRequestTo request)
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
            if (role != "ADMIN")
                return Error("Only ADMIN can update reactions", 403, 1);

            try
            {
                var updated = await _repository.UpdateAsync(request);
                if (updated == null)
                    return Error($"Reaction with id {request.Id} not found", 404, 1);
                return Ok(updated);
            }
            catch (TimeoutException)
            {
                return Error("Discussion service is busy", 504, 1);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error updating reaction");
                return Error("Internal server error", 500, 1);
            }
        }

        // DELETE /api/v2.0/reactions/{id} - только ADMIN
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

            var role = principal.FindFirst("role")?.Value;
            if (role != "ADMIN")
                return Error("Only ADMIN can delete reactions", 403, 1);

            try
            {
                await _repository.DeleteAsync(id);
                return NoContent();
            }
            catch (TimeoutException)
            {
                return Error("Discussion service is busy", 504, 1);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error deleting reaction");
                return Error("Internal server error", 500, 1);
            }
        }
    }
}