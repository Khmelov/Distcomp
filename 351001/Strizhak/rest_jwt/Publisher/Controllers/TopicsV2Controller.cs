using System.Security.Claims;
using Microsoft.AspNetCore.Mvc;
using Publisher.Dtos;
using Publisher.Entities;
using Publisher.Services;

namespace Publisher.Controllers
{
    [ApiController]
    [Route("api/v2.0/topics")]
    public class TopicsV2Controller : BaseApiController
    {
        private readonly IService<Topic, TopicRequestTo, TopicResponseTo> _topicService;
        private readonly IAuthService _authService;

        public TopicsV2Controller(
            IService<Topic, TopicRequestTo, TopicResponseTo> topicService,
            IAuthService authService)
        {
            _topicService = topicService;
            _authService = authService;
        }

        // ========== Вспомогательный метод ==========
        private async Task<ClaimsPrincipal?> GetPrincipalOrFail()
        {
            var authHeader = Request.Headers["Authorization"].ToString();
            var principal = await _authService.VerifyTokenAsync(authHeader);
            if (principal == null)
                throw new UnauthorizedAccessException();
            return principal;
        }

        // ========== GET /api/v2.0/topics/{id} ==========
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

            var topic = await _topicService.GetByIdAsync(id);
            if (topic == null)
                return Error($"Topic with id {id} not found", 404, 1);

            // Для чтения прав не проверяем – CanRead всегда true для CUSTOMER и ADMIN
            // Но если хотите явно – можно вызвать _authService.CanRead(principal, topic.UserId, currentUserId)
            return Ok(topic);
        }

        // ========== GET /api/v2.0/topics ==========
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

            var topics = await _topicService.GetAllAsync();
            return Ok(topics);
        }

        // ========== POST /api/v2.0/topics ==========
        [HttpPost]
        public async Task<IActionResult> Create([FromBody] TopicRequestTo request)
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

            // Получаем ID текущего пользователя из токена
            var currentUserId = long.Parse(principal.FindFirst(ClaimTypes.NameIdentifier)?.Value ?? "0");
            if (currentUserId == 0)
            {
                // Если в токене нет Id, можно получить через GetUserFromTokenAsync
                var user = await _authService.GetUserFromTokenAsync(Request.Headers["Authorization"].ToString());
                if (user == null)
                    return Error("User not found", 401, 2);
                currentUserId = user.Id;
            }

            // Устанавливаем владельца темы
            request.UserId = currentUserId;

            try
            {
                var created = await _topicService.CreateAsync(request);
                return CreatedAtAction(nameof(GetById), new { id = created.Id }, created);
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

        // ========== PUT /api/v2.0/topics ==========
        [HttpPut]
        public async Task<IActionResult> Update([FromBody] TopicRequestTo request)
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

            var currentUserId = long.Parse(principal.FindFirst(ClaimTypes.NameIdentifier)?.Value ?? "0");
            if (currentUserId == 0)
            {
                var user = await _authService.GetUserFromTokenAsync(Request.Headers["Authorization"].ToString());
                if (user == null)
                    return Error("User not found", 401, 2);
                currentUserId = user.Id;
            }

            // Получаем существующую тему, чтобы узнать её владельца
            var existingTopic = await _topicService.GetByIdAsync(request.Id);
            if (existingTopic == null)
                return Error($"Topic with id {request.Id} not found", 404, 1);

            // Проверка прав на изменение
            if (!_authService.CanModify(principal, existingTopic.UserId, currentUserId))
                return Error("Access denied", 403, 1);

            try
            {
                var updated = await _topicService.UpdateAsync(request.Id, request);
                return Ok(updated);
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

        // ========== DELETE /api/v2.0/topics/{id} ==========
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

            var currentUserId = long.Parse(principal.FindFirst(ClaimTypes.NameIdentifier)?.Value ?? "0");
            if (currentUserId == 0)
            {
                var user = await _authService.GetUserFromTokenAsync(Request.Headers["Authorization"].ToString());
                if (user == null)
                    return Error("User not found", 401, 2);
                currentUserId = user.Id;
            }

            var existingTopic = await _topicService.GetByIdAsync(id);
            if (existingTopic == null)
                return Error($"Topic with id {id} not found", 404, 1);

            if (!_authService.CanModify(principal, existingTopic.UserId, currentUserId))
                return Error("Access denied", 403, 1);

            try
            {
                await _topicService.DeleteAsync(id);
                return NoContent();
            }
            catch
            {
                return Error("Internal server error", 500, 1);
            }
        }
    }
}