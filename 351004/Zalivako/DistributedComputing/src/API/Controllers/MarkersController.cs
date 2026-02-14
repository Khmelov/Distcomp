using Application.DTOs.Requests;
using Application.DTOs.Responses;
using Application.Exceptions;
using Application.Interfaces;
using Microsoft.AspNetCore.Mvc;

namespace API.Controllers
{
    [ApiController]
    [Route("api/v1.0/[controller]")]
    public class MarkersController : ControllerBase
    {
        private readonly IMarkerService _markerService;
        private readonly ILogger<MarkersController> _logger;

        public MarkersController(IMarkerService markerService, ILogger<MarkersController> logger)
        {
            _markerService = markerService;
            _logger = logger;
        }

        [HttpPost]
        [ProducesResponseType(typeof(MarkerResponseTo), StatusCodes.Status201Created)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<MarkerResponseTo>> CreateMarker([FromBody] MarkerRequestTo createMarkerRequest)
        {
            try
            {
                _logger.LogInformation("Creating marker {request}", createMarkerRequest);

                MarkerResponseTo createdMarker = await _markerService.CreateMarker(createMarkerRequest);

                return CreatedAtAction(
                    nameof(CreateMarker),
                    new { id = createdMarker.Id },
                    createdMarker
                );
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error creating marker");
                return StatusCode(500, "An error occurred while creating the marker");
            }
        }

        [HttpGet]
        [ProducesResponseType(typeof(IEnumerable<MarkerResponseTo>), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<IEnumerable<MarkerResponseTo>>> GetAllMarkers()
        {
            try
            {
                _logger.LogInformation("Getting all markers");

                IEnumerable<MarkerResponseTo> markers = await _markerService.GetAllMarkers();

                return Ok(markers);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error getting all markers");
                return StatusCode(500, "An error occurred while retrieving markers");
            }
        }

        [HttpGet("{id:long}")]
        [ProducesResponseType(typeof(MarkerResponseTo), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<MarkerResponseTo>> GetMarkerById([FromRoute] long id)
        {
            try
            {
                _logger.LogInformation("Getting marker by id: {Id}", id);

                var getMarkerRequest = new MarkerRequestTo { Id = id };
                MarkerResponseTo marker = await _markerService.GetMarker(getMarkerRequest);

                return Ok(marker);
            }
            catch (NewNotFoundException ex)
            {
                _logger.LogWarning(ex, "Marker not found with id: {Id}", id);
                return NotFound($"Marker with id {id} not found");
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error getting marker by id: {Id}", id);
                return StatusCode(500, "An error occurred while retrieving the marker");
            }
        }

        [ProducesResponseType(typeof(MarkerResponseTo), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<MarkerResponseTo>> UpdateNews(
            [FromBody] MarkerRequestTo updateMarkerRequest)
        {
            try
            {
                _logger.LogInformation("Updating marker with id: {Id}", updateMarkerRequest.Id);

                var updatedMarker = await _markerService.UpdateMarker(updateMarkerRequest);

                return Ok(updatedMarker);
            }
            catch (NewNotFoundException ex)
            {
                _logger.LogWarning(ex, "Marker not found for update with id: {Id}", updateMarkerRequest.Id);
                return NotFound($"Marker with id {updateMarkerRequest.Id} not found");
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error updating marker with id: {Id}", updateMarkerRequest.Id);
                return StatusCode(500, "An error occurred while updating the marker");
            }
        }

        [HttpDelete("{id:long}")]
        [ProducesResponseType(StatusCodes.Status204NoContent)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<IActionResult> DeleteMarker([FromRoute] long id)
        {
            try
            {
                _logger.LogInformation("Deleting marker with id: {Id}", id);

                var deleteMarkerRequest = new MarkerRequestTo { Id = id };
                await _markerService.DeleteMarker(deleteMarkerRequest);

                return NoContent();
            }
            catch (MarkerNotFoundException ex)
            {
                _logger.LogWarning(ex, "Marker not found for deletion with id: {Id}", id);
                return NotFound();
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error deleting marker with id: {Id}", id);
                return StatusCode(500, "An error occurred while deleting the marker");
            }
        }
    }
}
