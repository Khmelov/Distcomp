using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using rest1.application.DTOs.requests;
using rest1.application.DTOs.responses;
using rest1.application.exceptions;
using rest1.application.interfaces.services;

namespace rest1.api.controllers.v2;

[ApiController]
[Route("api/v2.0/marks")]
[Authorize(AuthenticationSchemes = "Bearer")]
public class MarksV2Controller : ControllerBase
{
    private readonly IMarkService _markService;
    private readonly ILogger<MarksV2Controller> _logger;

    public MarksV2Controller(IMarkService markService, ILogger<MarksV2Controller> logger)
    {
        _markService = markService;
        _logger = logger;
    }

    [HttpGet]
    [AllowAnonymous]
    [ProducesResponseType(typeof(IEnumerable<MarkResponseTo>), StatusCodes.Status200OK)]
    public async Task<ActionResult<IEnumerable<MarkResponseTo>>> GetAllMarks()
    {
        try
        {
            _logger.LogInformation("Getting all marks");
            var marks = await _markService.GetAllMarks();
            return Ok(marks);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error getting all marks");
            return StatusCode(500, new { errorMessage = "An error occurred while retrieving marks", errorCode = "50016" });
        }
    }

    [HttpPost]
    [Authorize(Roles = "ADMIN")]
    [ProducesResponseType(typeof(MarkResponseTo), StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status403Forbidden)]
    [ProducesResponseType(StatusCodes.Status409Conflict)]
    public async Task<ActionResult<MarkResponseTo>> CreateMark([FromBody] MarkRequestTo createMarkRequest)
    {
        try
        {
            _logger.LogInformation("Creating mark {request}", createMarkRequest);

            var createdMark = await _markService.CreateMark(createMarkRequest);

            return CreatedAtAction(nameof(GetMarkById), new { id = createdMark.Id }, createdMark);
        }
        catch (MarkAlreadyExistsException ex)
        {
            _logger.LogError(ex, "Mark already exists");
            return Conflict(new { errorMessage = ex.Message, errorCode = "40903" });
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error creating mark");
            return StatusCode(500, new { errorMessage = "An error occurred while creating the mark", errorCode = "50017" });
        }
    }

    [HttpGet("{id:long}")]
    [AllowAnonymous]
    [ProducesResponseType(typeof(MarkResponseTo), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<ActionResult<MarkResponseTo>> GetMarkById([FromRoute] long id)
    {
        try
        {
            _logger.LogInformation("Getting mark by id: {Id}", id);

            var mark = await _markService.GetMark(new MarkRequestTo { Id = id });
            return Ok(mark);
        }
        catch (NewNotFoundException ex)
        {
            _logger.LogWarning(ex, "Mark not found with id: {Id}", id);
            return NotFound(new { errorMessage = ex.Message, errorCode = "40414" });
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error getting mark by id: {Id}", id);
            return StatusCode(500, new { errorMessage = "An error occurred while retrieving the mark", errorCode = "50018" });
        }
    }

    [HttpPut]
    [Authorize(Roles = "ADMIN")]
    [ProducesResponseType(typeof(MarkResponseTo), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status403Forbidden)]
    public async Task<ActionResult<MarkResponseTo>> UpdateMark([FromBody] MarkRequestTo updateMarkRequest)
    {
        try
        {
            _logger.LogInformation("Updating mark with id: {Id}", updateMarkRequest.Id);

            var updatedMark = await _markService.UpdateMark(updateMarkRequest);
            return Ok(updatedMark);
        }
        catch (NewNotFoundException ex)
        {
            _logger.LogWarning(ex, "Mark not found for update with id: {Id}", updateMarkRequest.Id);
            return NotFound(new { errorMessage = ex.Message, errorCode = "40415" });
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error updating mark with id: {Id}", updateMarkRequest.Id);
            return StatusCode(500, new { errorMessage = "An error occurred while updating the mark", errorCode = "50019" });
        }
    }

    [HttpDelete("{id:long}")]
    [Authorize(Roles = "ADMIN")]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status403Forbidden)]
    public async Task<IActionResult> DeleteMark([FromRoute] long id)
    {
        try
        {
            _logger.LogInformation("Deleting mark with id: {Id}", id);

            await _markService.DeleteMark(new MarkRequestTo { Id = id });
            return NoContent();
        }
        catch (MarkNotFoundException ex)
        {
            _logger.LogWarning(ex, "Mark not found for deletion with id: {Id}", id);
            return NotFound(new { errorMessage = ex.Message, errorCode = "40416" });
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error deleting mark with id: {Id}", id);
            return StatusCode(500, new { errorMessage = "An error occurred while deleting the mark", errorCode = "50020" });
        }
    }
}