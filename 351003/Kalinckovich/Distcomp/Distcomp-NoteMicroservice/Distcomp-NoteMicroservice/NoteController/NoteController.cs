using Distcomp_NoteMicroservice.Model.NoteModel.Dto;
using Distcomp_NoteMicroservice.Service;
using Distcomp_NoteMicroservice.Validation;
using Microsoft.AspNetCore.Mvc;

namespace Distcomp_NoteMicroservice.NoteController;

/// <summary>
/// Controller for managing Note entities.
/// </summary>
[ApiController]
[Route("api/[controller]")]
[Produces("application/json")]
public class NoteController : ControllerBase
{
    private readonly INoteService _noteService;
    private readonly ILogger<NoteController> _logger;

    public NoteController(
        INoteService noteService,
        ILogger<NoteController> logger)
    {
        _noteService = noteService;
        _logger = logger;
    }

    /// <summary>
    /// Get a specific note by country, topic ID, and note ID.
    /// </summary>
    /// <param name="country">Country code (ISO 3166-1 alpha-2 or alpha-3)</param>
    /// <param name="topicId">Topic ID (partition clustering key)</param>
    /// <param name="id">Note ID (clustering key)</param>
    /// <param name="cancellationToken">Cancellation token</param>
    /// <returns>Note details if found</returns>
    /// <response code="200">Returns the note</response>
    /// <response code="400">If validation fails</response>
    /// <response code="404">If note not found</response>
    [HttpGet("{country}/{topicId}/{id}")]
    [ProducesResponseType(StatusCodes.Status200OK, Type = typeof(NoteResponseDto))]
    [ProducesResponseType(StatusCodes.Status400BadRequest, Type = typeof(ProblemDetails))]
    [ProducesResponseType(StatusCodes.Status404NotFound, Type = typeof(ProblemDetails))]
    public async Task<ActionResult<NoteResponseDto>> GetNoteById(
        [FromRoute] string country,
        [FromRoute] long topicId,
        [FromRoute] long id,
        CancellationToken cancellationToken)
    {
        _logger.LogInformation(
            "GetNoteById request: country={Country}, topicId={TopicId}, id={Id}",
            country, topicId, id);

        var result = await _noteService.GetNoteByIdAsync(country, topicId, id, cancellationToken);

        // ✅ Map Result<T> to ActionResult<T>
        return MapResultToActionResult(result);
    }

    /// <summary>
    /// Get notes by country and topic with optional pagination.
    /// </summary>
    /// <param name="country">Country code</param>
    /// <param name="topicId">Topic ID</param>
    /// <param name="minId">Minimum note ID (inclusive)</param>
    /// <param name="maxId">Maximum note ID (inclusive)</param>
    /// <param name="limit">Maximum number of notes to return (1-1000)</param>
    /// <param name="cancellationToken">Cancellation token</param>
    /// <returns>List of notes</returns>
    /// <response code="200">Returns the list of notes</response>
    /// <response code="400">If validation fails</response>
    [HttpGet("{country}/{topicId}")]
    [ProducesResponseType(StatusCodes.Status200OK, Type = typeof(IEnumerable<NoteResponseDto>))]
    [ProducesResponseType(StatusCodes.Status400BadRequest, Type = typeof(ProblemDetails))]
    public async Task<ActionResult<IEnumerable<NoteResponseDto>>> GetNotesByCountryAndTopic(
        [FromRoute] string country,
        [FromRoute] long topicId,
        [FromQuery] long? minId = null,
        [FromQuery] long? maxId = null,
        [FromQuery] int limit = 100,
        CancellationToken cancellationToken = default)
    {
        _logger.LogInformation(
            "GetNotesByCountryAndTopic request: country={Country}, topicId={TopicId}, minId={MinId}, maxId={MaxId}, limit={Limit}",
            country, topicId, minId, maxId, limit);

        var result = await _noteService.GetNotesByCountryAndTopicAsync(
            country, topicId, minId, maxId, limit, cancellationToken);

        return MapResultToActionResult(result);
    }

    /// <summary>
    /// Create a new note.
    /// </summary>
    /// <param name="dto">Note creation data</param>
    /// <param name="cancellationToken">Cancellation token</param>
    /// <returns>Created note with generated ID</returns>
    /// <response code="201">Returns the created note</response>
    /// <response code="400">If validation fails</response>
    /// <response code="500">If server error occurs</response>
    [HttpPost]
    [ProducesResponseType(StatusCodes.Status201Created, Type = typeof(NoteResponseDto))]
    [ProducesResponseType(StatusCodes.Status400BadRequest, Type = typeof(ProblemDetails))]
    [ProducesResponseType(StatusCodes.Status500InternalServerError, Type = typeof(ProblemDetails))]
    public async Task<ActionResult<NoteResponseDto>> CreateNote(
        [FromBody] CreateNoteDto dto,
        CancellationToken cancellationToken)
    {
        _logger.LogInformation("CreateNote request: country={Country}, topicId={TopicId}", dto.Country, dto.TopicId);

        // ✅ Additional model state validation (from DataAnnotations)
        if (!ModelState.IsValid)
        {
            return BadRequest(CreateProblemDetails(
                "Validation failed",
                StatusCodes.Status400BadRequest,
                ModelState.Values
                    .SelectMany(v => v.Errors)
                    .Select(e => new ValidationError("request", e.ErrorMessage, "MODEL_STATE_INVALID"))
                    .ToArray()
            ));
        }

        var result = await _noteService.CreateNoteAsync(dto, cancellationToken);

        // ✅ Return 201 Created with Location header
        if (result.IsSuccess)
        {
            var locationUrl = Url.Action(
                nameof(GetNoteById),
                new { country = result.Value.Country, topicId = result.Value.TopicId, id = result.Value.Id });

            return Created(locationUrl, result.Value);
        }

        return MapResultToActionResult(result);
    }

    /// <summary>
    /// Update an existing note.
    /// </summary>
    /// <param name="dto">Note update data</param>
    /// <param name="cancellationToken">Cancellation token</param>
    /// <returns>Updated note</returns>
    /// <response code="200">Returns the updated note</response>
    /// <response code="400">If validation fails</response>
    /// <response code="404">If note not found</response>
    [HttpPut("{country}/{topicId}/{id}")]
    [ProducesResponseType(StatusCodes.Status200OK, Type = typeof(NoteResponseDto))]
    [ProducesResponseType(StatusCodes.Status400BadRequest, Type = typeof(ProblemDetails))]
    [ProducesResponseType(StatusCodes.Status404NotFound, Type = typeof(ProblemDetails))]
    public async Task<ActionResult<NoteResponseDto>> UpdateNote(
        [FromBody] UpdateNoteDto dto,
        CancellationToken cancellationToken)
    {
        _logger.LogInformation(
            "UpdateNote request: country={Country}, topicId={TopicId}, id={Id}",
            dto.Country, dto.TopicId, dto.Id);

        if (!ModelState.IsValid)
        {
            return BadRequest(CreateProblemDetails(
                "Validation failed",
                StatusCodes.Status400BadRequest,
                ModelState.Values
                    .SelectMany(v => v.Errors)
                    .Select(e => new ValidationError("request", e.ErrorMessage, "MODEL_STATE_INVALID"))
                    .ToArray()
            ));
        }

        var result = await _noteService.UpdateNoteAsync(dto, cancellationToken);

        return MapResultToActionResult(result);
    }

    /// <summary>
    /// Delete a note.
    /// </summary>
    /// <param name="country">Country code</param>
    /// <param name="topicId">Topic ID</param>
    /// <param name="id">Note ID</param>
    /// <param name="cancellationToken">Cancellation token</param>
    /// <returns>No content if successful</returns>
    /// <response code="204">Note deleted successfully</response>
    /// <response code="400">If validation fails</response>
    /// <response code="404">If note not found</response>
    [HttpDelete("{country}/{topicId}/{id}")]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    [ProducesResponseType(StatusCodes.Status400BadRequest, Type = typeof(ProblemDetails))]
    [ProducesResponseType(StatusCodes.Status404NotFound, Type = typeof(ProblemDetails))]
    public async Task<ActionResult<bool>> DeleteNote(
        [FromRoute] string country,
        [FromRoute] long topicId,
        [FromRoute] long id,
        CancellationToken cancellationToken)
    {
        _logger.LogInformation(
            "DeleteNote request: country={Country}, topicId={TopicId}, id={Id}",
            country, topicId, id);

        var result = await _noteService.DeleteNoteAsync(country, topicId, id, cancellationToken);

        if (result.IsSuccess)
        {
            return NoContent();
        }

        return MapResultToActionResult(result);
    }

    #region Helper Methods

    /// <summary>
    /// Maps Result&lt;T&gt; to ActionResult&lt;T&gt; with appropriate HTTP status codes.
    /// </summary>
    private ActionResult<T> MapResultToActionResult<T>(Result<T> result)
    {
        if (result.IsSuccess)
        {
            return Ok(result.Value);
        }

        // Determine HTTP status code based on error code
        var firstError = result.Errors.FirstOrDefault();
        var statusCode = firstError?.Code switch
        {
            "NOTE_NOT_FOUND" => StatusCodes.Status404NotFound,
            "NOTE_COUNTRY_REQUIRED" or "NOTE_COUNTRY_INVALID_LENGTH" or "NOTE_COUNTRY_INVALID_FORMAT" => StatusCodes.Status400BadRequest,
            "NOTE_TOPIC_ID_INVALID" => StatusCodes.Status400BadRequest,
            "NOTE_ID_INVALID" => StatusCodes.Status400BadRequest,
            "NOTE_CONTENT_REQUIRED" or "NOTE_CONTENT_TOO_SHORT" or "NOTE_CONTENT_TOO_LONG" => StatusCodes.Status400BadRequest,
            "NOTE_LIMIT_INVALID" => StatusCodes.Status400BadRequest,
            "NOTE_FETCH_ERROR" or "NOTE_CREATE_ERROR" or "NOTE_UPDATE_ERROR" or "NOTE_DELETE_ERROR" => StatusCodes.Status500InternalServerError,
            _ => StatusCodes.Status400BadRequest
        };

        return StatusCode(statusCode, CreateProblemDetails(
            firstError?.Message ?? "An error occurred",
            statusCode,
            result.Errors.ToArray()
        ));
    }

    /// <summary>
    /// Creates a ProblemDetails response (RFC 7807 compliant).
    /// </summary>
    private static ProblemDetails CreateProblemDetails(
        string title,
        int statusCode,
        params ValidationError[] errors)
    {
        return new ProblemDetails
        {
            Title = title,
            Status = statusCode,
            Type = $"https://httpstatuses.com/{statusCode}",
            Detail = string.Join("; ", errors.Select(e => e.Message)),
            Extensions =
            {
                ["errors"] = errors.Select(e => new
                {
                    property = e.Property,
                    message = e.Message,
                    code = e.Code
                }).ToList()
            }
        };
    }

    #endregion
}