using Microsoft.AspNetCore.Mvc;
using Task310RestApi.DTOs.Request;
using Task310RestApi.DTOs.Response;
using Task310RestApi.Interfaces;
using Task310RestApi.Exceptions;

namespace Task310RestApi.Controllers
{
    [Route("api/v1.0/news")]
    [ApiController]
    public class NewsController : ControllerBase
    {
        private readonly INewsService _newsService;
        public NewsController(INewsService newsService) => _newsService = newsService;

        [HttpGet]
        public async Task<ActionResult<IEnumerable<NewsResponseTo>>> GetNews()
        {
            var news = await _newsService.GetAllNewsAsync();
            return Ok(news);
        }

        // Явно задаем имя маршрута "GetNewsById"
        [HttpGet("{id}", Name = "GetNewsById")] 
        public async Task<ActionResult<NewsResponseTo>> GetNewsById(long id) 
        {
            var news = await _newsService.GetNewsByIdAsync(id);
            return Ok(news);
        }

        [HttpPost]
        public async Task<ActionResult<NewsResponseTo>> CreateNews([FromBody] NewsRequestTo newsRequest)
        {
            try
            {
                var createdNews = await _newsService.CreateNewsAsync(newsRequest);
                // Используем строку "GetNewsById", чтобы избежать ошибок сопоставления
                return CreatedAtAction("GetNewsById", new { id = createdNews.Id }, createdNews);
            }
            catch (ValidationException ex)
            {
                return BadRequest(new ErrorResponse(ex.Message, ex.ErrorCode));
            }
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteNews(long id)
        {
            await _newsService.DeleteNewsAsync(id);
            return NoContent();
        }

        [HttpPut("{id}")]
        public async Task<ActionResult<NewsResponseTo>> UpdateNews(long id, [FromBody] NewsRequestTo newsRequest)
        {
            try
            {
                var updatedNews = await _newsService.UpdateNewsAsync(id, newsRequest);
                return Ok(updatedNews);
            }
            catch (ValidationException ex)
            {
                return BadRequest(new ErrorResponse(ex.Message, ex.ErrorCode));
            }
        }

        [HttpGet("search")]
        public async Task<ActionResult<IEnumerable<NewsResponseTo>>> SearchNews(
            [FromQuery] List<string>? labelNames,
            [FromQuery] List<long>? labelIds,
            [FromQuery] string? creatorLogin,
            [FromQuery] string? title,
            [FromQuery] string? content)
        {
            var news = await _newsService.GetNewsByParamsAsync(labelNames, labelIds, creatorLogin, title, content);
            return Ok(news);
        }

        // Заглушка для PUT без ID (для тестов на 4xx)
        [HttpPut]
        public IActionResult UpdateNewsNoId()
        {
            return BadRequest(new ErrorResponse("Id must be in URL", "40000"));
        }
    }
}