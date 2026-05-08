using Application.DTOs.Requests;
using Application.DTOs.Responses;
using Application.Interfaces;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace API.Controllers.V2
{
    [ApiController]
    [Route("api/v2.0/[controller]")]
    [Authorize(Roles = "ADMIN,CUSTOMER")]
    public class NewsController : ControllerBase
    {
        private readonly INewsService _newsService;
        private readonly ILogger<NewsController> _logger;

        public NewsController(INewsService newsService, ILogger<NewsController> logger)
        {
            _newsService = newsService;
            _logger = logger;
        }

        [HttpPost]
        public async Task<ActionResult<NewsResponseTo>> CreateNews([FromBody] NewsRequestTo request)
        {
            if (!IsAdmin() && request.EditorId != GetCurrentUserId())
                return Forbid();

            var created = await _newsService.CreateNews(request);
            return CreatedAtAction(nameof(GetNewsById), new { id = created.Id }, created);
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<NewsResponseTo>>> GetAllNews()
        {
            var news = await _newsService.GetAllNews();
            return Ok(news);
        }

        [HttpGet("{id:long}")]
        public async Task<ActionResult<NewsResponseTo>> GetNewsById(long id)
        {
            var news = await _newsService.GetNews(new NewsRequestTo { Id = id });
            return Ok(news);
        }

        [HttpPut("{id:long}")]
        public async Task<ActionResult<NewsResponseTo>> UpdateNews(long id, [FromBody] NewsRequestTo request)
        {
            // Проверка владельца
            var existing = await _newsService.GetNews(new NewsRequestTo { Id = id });
            if (!IsAdmin() && existing.EditorId != GetCurrentUserId())
                return Forbid();

            request.Id = id;
            var updated = await _newsService.UpdateNews(request);
            return Ok(updated);
        }

        [HttpDelete("{id:long}")]
        public async Task<IActionResult> DeleteNews(long id)
        {
            var existing = await _newsService.GetNews(new NewsRequestTo { Id = id });
            if (!IsAdmin() && existing.EditorId != GetCurrentUserId())
                return Forbid();

            await _newsService.DeleteNews(new NewsRequestTo { Id = id });
            return NoContent();
        }

        private long GetCurrentUserId()
        {
            var idClaim = User.FindFirst("id");
            return idClaim != null ? long.Parse(idClaim.Value) : 0;
        }

        private bool IsAdmin()
        {
            return User.IsInRole("ADMIN");
        }
    }
}