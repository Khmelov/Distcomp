using Microsoft.AspNetCore.Mvc;
using rest1.application.DTOs.requests;
using rest1.application.DTOs.responses;
using rest1.application.exceptions;
using rest1.application.interfaces.services;

namespace rest1.api.controllers;

    [ApiController]
    [Route("api/v1.0/[controller]")]
    public class CreatorsController : ControllerBase
    {
        private readonly ICreatorService _creatorService;
        private readonly ILogger<CreatorsController> _logger;

        public CreatorsController(ICreatorService creatorService, ILogger<CreatorsController> logger)
        {
            _creatorService = creatorService;
            _logger = logger;
        }

        [HttpPost]
        [ProducesResponseType(typeof(CreatorResponseTo), StatusCodes.Status201Created)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<CreatorResponseTo>> CreateCreator(
            [FromBody] CreatorRequestTo createCreatorRequest)
        {
            try
            {
                _logger.LogInformation("Creating creator {request}", createCreatorRequest);

                CreatorResponseTo createdCreator =
                    await _creatorService.CreateCreator(createCreatorRequest);

                return CreatedAtAction(
                    nameof(CreateCreator),
                    new { id = createdCreator.Id },
                    createdCreator
                );
            }
            catch (CreatorAlreadyExistsException ex)
            {
                _logger.LogError(ex, "Creator already exists");
                return StatusCode(403);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error creating creator");
                return StatusCode(500, "An error occurred while creating the creator");
            }
        }

        [HttpGet]
        [ProducesResponseType(typeof(IEnumerable<CreatorResponseTo>), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<IEnumerable<CreatorResponseTo>>> GetAllCreators()
        {
            try
            {
                _logger.LogInformation("Getting all creators");

                IEnumerable<CreatorResponseTo> creators =
                    await _creatorService.GetAllCreators();

                return Ok(creators);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error getting all creators");
                return StatusCode(500, "An error occurred while retrieving creators");
            }
        }

        [HttpGet("{id:long}")]
        [ProducesResponseType(typeof(CreatorResponseTo), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<CreatorResponseTo>> GetCreatorById([FromRoute] long id)
        {
            try
            {
                _logger.LogInformation("Getting creator by id: {Id}", id);

                var getCreatorRequest = new CreatorRequestTo { Id = id };
                CreatorResponseTo creator =
                    await _creatorService.GetCreator(getCreatorRequest);

                return Ok(creator);
            }
            catch (NewNotFoundException ex)
            {
                _logger.LogWarning(ex, "Creator not found with id: {Id}", id);
                return NotFound($"Creator with id {id} not found");
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error getting creator by id: {Id}", id);
                return StatusCode(500, "An error occurred while retrieving the creator");
            }
        }

        [HttpPut]
        [ProducesResponseType(typeof(CreatorResponseTo), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<CreatorResponseTo>> UpdateCreator(
            [FromBody] CreatorRequestTo updateCreatorRequest)
        {
            try
            {
                _logger.LogInformation(
                    "Updating creator with id: {Id}",
                    updateCreatorRequest.Id);

                var updatedCreator =
                    await _creatorService.UpdateCreator(updateCreatorRequest);

                return Ok(updatedCreator);
            }
            catch (NewNotFoundException ex)
            {
                _logger.LogWarning(
                    ex,
                    "Creator not found for update with id: {Id}",
                    updateCreatorRequest.Id);

                return NotFound(
                    $"Creator with id {updateCreatorRequest.Id} not found");
            }
            catch (Exception ex)
            {
                _logger.LogError(
                    ex,
                    "Error updating creator with id: {Id}",
                    updateCreatorRequest.Id);

                return StatusCode(
                    500,
                    "An error occurred while updating the creator");
            }
        }

        [HttpDelete("{id:long}")]
        [ProducesResponseType(StatusCodes.Status204NoContent)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<IActionResult> DeleteCreator([FromRoute] long id)
        {
            try
            {
                _logger.LogInformation("Deleting creator with id: {Id}", id);

                var deleteCreatorRequest = new CreatorRequestTo { Id = id };
                await _creatorService.DeleteCreator(deleteCreatorRequest);

                return NoContent();
            }
            catch (CreatorNotFoundException ex)
            {
                _logger.LogWarning(
                    ex,
                    "Creator not found for deletion with id: {Id}",
                    id);

                return NotFound();
            }
            catch (Exception ex)
            {
                _logger.LogError(
                    ex,
                    "Error deleting creator with id: {Id}",
                    id);

                return StatusCode(
                    500,
                    "An error occurred while deleting the creator");
            }
        }
    }