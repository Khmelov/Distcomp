using System.Security.Claims;
using Microsoft.AspNetCore.Mvc;
using Publisher.Dtos;
using Publisher.Entities;
using Publisher.Services;

namespace Publisher.Controllers
{
    [ApiController]
    [Route("api/v2.0/tags")]
    public class TagsV2Controller : BaseApiController
    {
        private readonly IService<Tag, TagRequestTo, TagResponseTo> _tagService;
        private readonly IAuthService _authService;

        public TagsV2Controller(
            IService<Tag, TagRequestTo, TagResponseTo> tagService,
            IAuthService authService)
        {
            _tagService = tagService;
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

        // GET /api/v2.0/tags/{id} - чтение для всех аутентифицированных
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

            var tag = await _tagService.GetByIdAsync(id);
            if (tag == null)
                return Error($"Tag with id {id} not found", 404, 1);

            return Ok(tag);
        }

        // GET /api/v2.0/tags - чтение для всех аутентифицированных
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

            var tags = await _tagService.GetAllAsync();
            return Ok(tags);
        }

        // POST /api/v2.0/tags - только ADMIN
        [HttpPost]
        public async Task<IActionResult> Create([FromBody] TagRequestTo request)
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
                return Error("Only ADMIN can create tags", 403, 1);

            try
            {
                var created = await _tagService.CreateAsync(request);
                return CreatedAtAction(nameof(GetById), new { id = created.Id }, created);
            }
            catch (InvalidOperationException ex)
            {
                return Error(ex.Message, 409, 1);
            }
            catch (Exception)
            {
                return Error("Internal server error", 500, 1);
            }
        }

        // PUT /api/v2.0/tags/{id} - только ADMIN
        [HttpPut("{id:long}")]
        public async Task<IActionResult> Update(long id, [FromBody] TagRequestTo request)
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
                return Error("Only ADMIN can update tags", 403, 1);

            try
            {
                var updated = await _tagService.UpdateAsync(id, request);
                return Ok(updated);
            }
            catch (KeyNotFoundException)
            {
                return Error($"Tag with id {id} not found", 404, 1);
            }
            catch (InvalidOperationException ex)
            {
                return Error(ex.Message, 409, 1);
            }
            catch (Exception)
            {
                return Error("Internal server error", 500, 1);
            }
        }

        // DELETE /api/v2.0/tags/{id} - только ADMIN
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
                return Error("Only ADMIN can delete tags", 403, 1);

            try
            {
                await _tagService.DeleteAsync(id);
                return NoContent();
            }
            catch (KeyNotFoundException)
            {
                return Error($"Tag with id {id} not found", 404, 1);
            }
            catch (Exception)
            {
                return Error("Internal server error", 500, 1);
            }
        }
    }
}