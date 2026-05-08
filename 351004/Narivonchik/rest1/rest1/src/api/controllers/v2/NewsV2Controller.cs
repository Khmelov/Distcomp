using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using rest1.application.DTOs.requests;
using rest1.application.DTOs.responses;
using rest1.application.exceptions;
using rest1.application.interfaces.services;

namespace rest1.api.controllers.v2;

[ApiController]
[Route("api/v2.0/news")]
[Authorize(AuthenticationSchemes = "Bearer")]
public class NewsV2Controller : ControllerBase
{
    private readonly INewsService _newsService;
    private readonly ICreatorService _creatorService;
    private readonly ILogger<NewsV2Controller> _logger;

    public NewsV2Controller(INewsService newsService, ICreatorService creatorService, ILogger<NewsV2Controller> logger)
    {
        _newsService = newsService;
        _creatorService = creatorService;
        _logger = logger;
    }

    private async Task<bool> CanModifyNews(long newsId, string currentUserLogin)
    {
        var news = await _newsService.GetNews(new NewsRequestTo { Id = newsId });
        var creator = await _creatorService.GetCreator(new CreatorRequestTo { Id = news.CreatorId });
        return creator.Login == currentUserLogin;
    }

    [HttpGet]
    [AllowAnonymous]
    [ProducesResponseType(typeof(IEnumerable<NewsResponseTo>), StatusCodes.Status200OK)]
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
            return StatusCode(500, new { errorMessage = "An error occurred while retrieving news", errorCode = "50006" });
        }
    }

    [HttpPost]
    [ProducesResponseType(typeof(NewsResponseTo), StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status403Forbidden)]
    public async Task<ActionResult<NewsResponseTo>> CreateNews([FromBody] NewsRequestTo createNewsRequest)
    {
        try
        {
            _logger.LogInformation("Creating news with title: {Title}", createNewsRequest.Title);
            
            var isAdmin = User.IsInRole("ADMIN");
            var currentUserLogin = User.FindFirst("sub")?.Value;
            
            // Verify ownership
            var creator = await _creatorService.GetCreator(new CreatorRequestTo { Id = createNewsRequest.CreatorId });
            if (!isAdmin && creator.Login != currentUserLogin)
            {
                return Forbid();
            }

            var createdNews = await _newsService.CreateNews(createNewsRequest);
            return CreatedAtAction(nameof(GetNewsById), new { id = createdNews.Id }, createdNews);
        }
        catch (NewsAlreadyExistsException ex)
        {
            return Conflict(new { errorMessage = ex.Message, errorCode = "40902" });
        }
        catch (NewsReferenceException ex)
        {
            return NotFound(new { errorMessage = ex.Message, errorCode = "40405" });
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error creating news");
            return StatusCode(500, new { errorMessage = "An error occurred while creating the news", errorCode = "50007" });
        }
    }

    [HttpGet("{id:long}")]
    [AllowAnonymous]
    [ProducesResponseType(typeof(NewsResponseTo), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<ActionResult<NewsResponseTo>> GetNewsById([FromRoute] long id)
    {
        try
        {
            _logger.LogInformation("Getting news by id: {Id}", id);
            var news = await _newsService.GetNews(new NewsRequestTo { Id = id });
            return Ok(news);
        }
        catch (NewNotFoundException ex)
        {
            _logger.LogWarning(ex, "News not found with id: {Id}", id);
            return NotFound(new { errorMessage = ex.Message, errorCode = "40406" });
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error getting news by id: {Id}", id);
            return StatusCode(500, new { errorMessage = "An error occurred while retrieving the news", errorCode = "50008" });
        }
    }

    [HttpPut]
    [ProducesResponseType(typeof(NewsResponseTo), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status403Forbidden)]
    public async Task<ActionResult<NewsResponseTo>> UpdateNews([FromBody] NewsRequestTo updateNewsRequest)
    {
        try
        {
            _logger.LogInformation("Updating news with id: {Id}", updateNewsRequest.Id);
            
            var isAdmin = User.IsInRole("ADMIN");
            var currentUserLogin = User.FindFirst("sub")?.Value;
            
            if (!isAdmin && !await CanModifyNews(updateNewsRequest.Id!.Value, currentUserLogin!))
            {
                return Forbid();
            }

            var updatedNews = await _newsService.UpdateNews(updateNewsRequest);
            return Ok(updatedNews);
        }
        catch (NewNotFoundException ex)
        {
            _logger.LogWarning(ex, "News not found for update with id: {Id}", updateNewsRequest.Id);
            return NotFound(new { errorMessage = ex.Message, errorCode = "40407" });
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error updating news with id: {Id}", updateNewsRequest.Id);
            return StatusCode(500, new { errorMessage = "An error occurred while updating the news", errorCode = "50009" });
        }
    }

    [HttpDelete("{id:long}")]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status403Forbidden)]
    public async Task<IActionResult> DeleteNews([FromRoute] long id)
    {
        try
        {
            _logger.LogInformation("Deleting news with id: {Id}", id);
            
            var isAdmin = User.IsInRole("ADMIN");
            var currentUserLogin = User.FindFirst("sub")?.Value;
            
            if (!isAdmin && !await CanModifyNews(id, currentUserLogin!))
            {
                return Forbid();
            }

            await _newsService.DeleteNews(new NewsRequestTo { Id = id });
            return NoContent();
        }
        catch (NewNotFoundException ex)
        {
            _logger.LogWarning(ex, "News not found for deletion with id: {Id}", id);
            return NotFound(new { errorMessage = ex.Message, errorCode = "40408" });
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error deleting news with id: {Id}", id);
            return StatusCode(500, new { errorMessage = "An error occurred while deleting the news", errorCode = "50010" });
        }
    }
}