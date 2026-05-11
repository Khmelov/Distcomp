using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using rest1.application.DTOs.requests;
using rest1.application.DTOs.responses;
using rest1.application.exceptions;
using rest1.application.interfaces.services;

namespace rest1.api.controllers.v2;

[ApiController]
[Route("api/v2.0/creators")]
[Authorize(AuthenticationSchemes = "Bearer")]
public class CreatorsV2Controller : ControllerBase
{
    private readonly ICreatorService _creatorService;
    private readonly ILogger<CreatorsV2Controller> _logger;

    public CreatorsV2Controller(ICreatorService creatorService, ILogger<CreatorsV2Controller> logger)
    {
        _creatorService = creatorService;
        _logger = logger;
    }

    [HttpPost("register")]
    [AllowAnonymous]
    [ProducesResponseType(typeof(CreatorResponseTo), StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status409Conflict)]
    public async Task<ActionResult<CreatorResponseTo>> Register([FromBody] RegisterRequestTo registerRequest)
    {
        try
        {
            _logger.LogInformation("Registering new creator: {Login}", registerRequest.Login);

            var creatorRequest = new CreatorRequestTo
            {
                Login = registerRequest.Login,
                Password = registerRequest.Password,
                Firstname = registerRequest.Firstname,
                Lastname = registerRequest.Lastname
            };

            var createdCreator = await _creatorService.CreateCreator(creatorRequest);
            return CreatedAtAction(nameof(GetCreatorById), new { id = createdCreator.Id }, createdCreator);
        }
        catch (CreatorAlreadyExistsException ex)
        {
            _logger.LogError(ex, "Creator already exists");
            return Conflict(new { errorMessage = ex.Message, errorCode = "40901" });
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error creating creator");
            return StatusCode(500, new { errorMessage = "An error occurred while creating the creator", errorCode = "50001" });
        }
    }

    [HttpGet]
    [Authorize(Roles = "ADMIN")]
    [ProducesResponseType(typeof(IEnumerable<CreatorResponseTo>), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status403Forbidden)]
    public async Task<ActionResult<IEnumerable<CreatorResponseTo>>> GetAllCreators()
    {
        try
        {
            _logger.LogInformation("Getting all creators");
            var creators = await _creatorService.GetAllCreators();
            return Ok(creators);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error getting all creators");
            return StatusCode(500, new { errorMessage = "An error occurred while retrieving creators", errorCode = "50002" });
        }
    }

    [HttpGet("{id:long}")]
    [ProducesResponseType(typeof(CreatorResponseTo), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status403Forbidden)]
    public async Task<ActionResult<CreatorResponseTo>> GetCreatorById([FromRoute] long id)
    {
        try
        {
            _logger.LogInformation("Getting creator by id: {Id}", id);
            
            var isAdmin = User.IsInRole("ADMIN");
            var currentUserLogin = User.FindFirst("sub")?.Value;
            
            var creator = await _creatorService.GetCreator(new CreatorRequestTo { Id = id });
            
            // CUSTOMER can only view their own profile
            if (!isAdmin && creator.Login != currentUserLogin)
            {
                return Forbid();
            }

            return Ok(creator);
        }
        catch (NewNotFoundException ex)
        {
            _logger.LogWarning(ex, "Creator not found with id: {Id}", id);
            return NotFound(new { errorMessage = ex.Message, errorCode = "40402" });
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error getting creator by id: {Id}", id);
            return StatusCode(500, new { errorMessage = "An error occurred while retrieving the creator", errorCode = "50003" });
        }
    }

    [HttpPut]
    [ProducesResponseType(typeof(CreatorResponseTo), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status403Forbidden)]
    public async Task<ActionResult<CreatorResponseTo>> UpdateCreator([FromBody] CreatorRequestTo updateCreatorRequest)
    {
        try
        {
            _logger.LogInformation("Updating creator with id: {Id}", updateCreatorRequest.Id);
            
            var isAdmin = User.IsInRole("ADMIN");
            var currentUserLogin = User.FindFirst("sub")?.Value;
            
            var existingCreator = await _creatorService.GetCreator(new CreatorRequestTo { Id = updateCreatorRequest.Id!.Value });
            
            if (!isAdmin && existingCreator.Login != currentUserLogin)
            {
                return Forbid();
            }

            var updatedCreator = await _creatorService.UpdateCreator(updateCreatorRequest);
            return Ok(updatedCreator);
        }
        catch (NewNotFoundException ex)
        {
            _logger.LogWarning(ex, "Creator not found for update with id: {Id}", updateCreatorRequest.Id);
            return NotFound(new { errorMessage = ex.Message, errorCode = "40403" });
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error updating creator with id: {Id}", updateCreatorRequest.Id);
            return StatusCode(500, new { errorMessage = "An error occurred while updating the creator", errorCode = "50004" });
        }
    }

    [HttpDelete("{id:long}")]
    [Authorize(Roles = "ADMIN")]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status403Forbidden)]
    public async Task<IActionResult> DeleteCreator([FromRoute] long id)
    {
        try
        {
            _logger.LogInformation("Deleting creator with id: {Id}", id);
            await _creatorService.DeleteCreator(new CreatorRequestTo { Id = id });
            return NoContent();
        }
        catch (CreatorNotFoundException ex)
        {
            _logger.LogWarning(ex, "Creator not found for deletion with id: {Id}", id);
            return NotFound(new { errorMessage = ex.Message, errorCode = "40404" });
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error deleting creator with id: {Id}", id);
            return StatusCode(500, new { errorMessage = "An error occurred while deleting the creator", errorCode = "50005" });
        }
    }
}