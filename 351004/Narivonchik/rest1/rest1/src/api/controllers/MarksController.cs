using Microsoft.AspNetCore.Mvc;
using rest1.application.DTOs.requests;
using rest1.application.DTOs.responses;
using rest1.application.exceptions;
using rest1.application.interfaces.services;

namespace rest1.api.controllers;

[ApiController]
[Route("api/v1.0/[controller]")]
public class MarksController : ControllerBase
{
    private readonly IMarkService _markService;
    private readonly ILogger<MarksController> _logger;

    public MarksController(IMarkService markService, ILogger<MarksController> logger)
    {
        _markService = markService;
        _logger = logger;
    }

    [HttpPost]
    [ProducesResponseType(typeof(MarkResponseTo), StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status500InternalServerError)]
    public async Task<ActionResult<MarkResponseTo>> CreateMark(
        [FromBody] MarkRequestTo createMarkRequest)
    {
        try
        {
            _logger.LogInformation("Creating mark {request}", createMarkRequest);

            MarkResponseTo createdMark =
                await _markService.CreateMark(createMarkRequest);

            return CreatedAtAction(
                nameof(CreateMark),
                new { id = createdMark.Id },
                createdMark
            );
        }
        catch (MarkAlreadyExistsException ex)
        {
            _logger.LogError(ex, "Mark already exists");
            return StatusCode(403);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error creating mark");
            return StatusCode(500, "An error occurred while creating the mark");
        }
    }

    [HttpGet]
    [ProducesResponseType(typeof(IEnumerable<MarkResponseTo>), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status500InternalServerError)]
    public async Task<ActionResult<IEnumerable<MarkResponseTo>>> GetAllMarks()
    {
        try
        {
            _logger.LogInformation("Getting all marks");

            IEnumerable<MarkResponseTo> marks =
                await _markService.GetAllMarks();

            return Ok(marks);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error getting all marks");
            return StatusCode(500, "An error occurred while retrieving marks");
        }
    }

    [HttpGet("{id:long}")]
    [ProducesResponseType(typeof(MarkResponseTo), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status500InternalServerError)]
    public async Task<ActionResult<MarkResponseTo>> GetMarkById([FromRoute] long id)
    {
        try
        {
            _logger.LogInformation("Getting mark by id: {Id}", id);

            var getMarkRequest = new MarkRequestTo { Id = id };
            MarkResponseTo mark =
                await _markService.GetMark(getMarkRequest);

            return Ok(mark);
        }
        catch (NewNotFoundException ex)
        {
            _logger.LogWarning(ex, "Mark not found with id: {Id}", id);
            return NotFound($"Mark with id {id} not found");
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error getting mark by id: {Id}", id);
            return StatusCode(500, "An error occurred while retrieving the mark");
        }
    }

    [HttpPut]
    [ProducesResponseType(typeof(MarkResponseTo), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status500InternalServerError)]
    public async Task<ActionResult<MarkResponseTo>> UpdateMark(
        [FromBody] MarkRequestTo updateMarkRequest)
    {
        try
        {
            _logger.LogInformation("Updating mark with id: {Id}", updateMarkRequest.Id);

            var updatedMark =
                await _markService.UpdateMark(updateMarkRequest);

            return Ok(updatedMark);
        }
        catch (NewNotFoundException ex)
        {
            _logger.LogWarning(
                ex,
                "Mark not found for update with id: {Id}",
                updateMarkRequest.Id);

            return NotFound(
                $"Mark with id {updateMarkRequest.Id} not found");
        }
        catch (Exception ex)
        {
            _logger.LogError(
                ex,
                "Error updating mark with id: {Id}",
                updateMarkRequest.Id);

            return StatusCode(
                500,
                "An error occurred while updating the mark");
        }
    }

    [HttpDelete("{id:long}")]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status500InternalServerError)]
    public async Task<IActionResult> DeleteMark([FromRoute] long id)
    {
        try
        {
            _logger.LogInformation("Deleting mark with id: {Id}", id);

            var deleteMarkRequest = new MarkRequestTo { Id = id };
            await _markService.DeleteMark(deleteMarkRequest);

            return NoContent();
        }
        catch (MarkNotFoundException ex)
        {
            _logger.LogWarning(ex, "Mark not found for deletion with id: {Id}", id);
            return NotFound();
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error deleting mark with id: {Id}", id);
            return StatusCode(500, "An error occurred while deleting the mark");
        }
    }
}
