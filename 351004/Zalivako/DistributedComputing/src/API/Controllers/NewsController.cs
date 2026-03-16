using Microsoft.AspNetCore.Mvc;
using Application.DTOs.Requests;
using Application.DTOs.Responses;
using Application.Interfaces;
using Application.Exceptions;

namespace API.Controllers
{
    [ApiController]
    [Route("api/v1.0/[controller]")]
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
        [ProducesResponseType(typeof(NewsResponseTo), StatusCodes.Status201Created)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<NewsResponseTo>> CreateNews([FromBody] NewsRequestTo createNewsRequest)
        {
            try
            {
                _logger.LogInformation("Creating news with title: {Title}", createNewsRequest.Title);

                var createdNews = await _newsService.CreateNews(createNewsRequest);

                return CreatedAtAction(
                    nameof(GetNewsById),
                    new { id = createdNews.Id },
                    createdNews
                );
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error creating news");
                return StatusCode(500, "An error occurred while creating the news");
            }
        }

        [HttpGet]
        [ProducesResponseType(typeof(IEnumerable<NewsResponseTo>), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<IEnumerable<NewsResponseTo>>> GetAllNews()
        {
            try
            {
                _logger.LogInformation("Getting all news");

                var news = await _newsService.GetAllNews();

                return Ok(news);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error getting all news");
                return StatusCode(500, "An error occurred while retrieving news");
            }
        }

        [HttpGet("{id:long}")]
        [ProducesResponseType(typeof(NewsResponseTo), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<NewsResponseTo>> GetNewsById([FromRoute] long id)
        {
            try
            {
                _logger.LogInformation("Getting news by id: {Id}", id);

                var getNewsRequest = new NewsRequestTo { Id = id };
                var news = await _newsService.GetNews(getNewsRequest);

                return Ok(news);
            }
            catch (NewNotFoundException ex)
            {
                _logger.LogWarning(ex, "News not found with id: {Id}", id);
                return NotFound($"News with id {id} not found");
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error getting news by id: {Id}", id);
                return StatusCode(500, "An error occurred while retrieving the news");
            }
        }

        [HttpPut]
        [ProducesResponseType(typeof(NewsResponseTo), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<NewsResponseTo>> UpdateNews(
            [FromBody] NewsRequestTo updateNewsRequest)
        {
            try
            {
                _logger.LogInformation("Updating news with id: {Id}", updateNewsRequest.Id);

                var updatedNews = await _newsService.UpdateNews(updateNewsRequest);

                return Ok(updatedNews);
            }
            catch (NewNotFoundException ex)
            {
                _logger.LogWarning(ex, "News not found for update with id: {Id}", updateNewsRequest.Id);
                return NotFound();
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error updating news with id: {Id}", updateNewsRequest.Id);
                return StatusCode(500, "An error occurred while updating the news");
            }
        }

        [HttpDelete("{id:long}")]
        [ProducesResponseType(StatusCodes.Status204NoContent)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<IActionResult> DeleteNews([FromRoute] long id)
        {
            try
            {
                _logger.LogInformation("Deleting news with id: {Id}", id);

                var deleteNewsRequest = new NewsRequestTo { Id = id };
                await _newsService.DeleteNews(deleteNewsRequest);

                return NoContent();
            }
            catch (NewNotFoundException ex)
            {
                _logger.LogWarning(ex, "News not found for deletion with id: {Id}", id);
                return NotFound();
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error deleting news with id: {Id}", id);
                return StatusCode(500, "An error occurred while deleting the news");
            }
        }
    }
}