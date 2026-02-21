using Application.DTOs.Requests;
using Application.DTOs.Responses;
using Application.Exceptions;
using Application.Interfaces;
using Microsoft.AspNetCore.Mvc;

namespace API.Controllers
{
    [ApiController]
    [Route("api/v1.0/[controller]")]
    public class EditorsController : ControllerBase
    {
        private readonly IEditorService _editorService;
        private readonly ILogger<EditorsController> _logger;

        public EditorsController(IEditorService editorService, ILogger<EditorsController> logger)
        {
            _editorService = editorService;
            _logger = logger;
        }

        [HttpPost]
        [ProducesResponseType(typeof(EditorResponseTo), StatusCodes.Status201Created)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<EditorResponseTo>> CreateEditor([FromBody] EditorRequestTo createEditorRequest)
        {
            try
            {
                _logger.LogInformation("Creating editor {request}", createEditorRequest);

                EditorResponseTo createdEditor = await _editorService.CreateEditor(createEditorRequest);

                return CreatedAtAction(
                    nameof(CreateEditor),
                    new { id = createdEditor.Id },
                    createdEditor
                );
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error creating editor");
                return StatusCode(500, "An error occurred while creating the editor");
            }
        }

        [HttpGet]
        [ProducesResponseType(typeof(IEnumerable<EditorResponseTo>), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<IEnumerable<EditorResponseTo>>> GetAllEditors()
        {
            try
            {
                _logger.LogInformation("Getting all editors");

                IEnumerable<EditorResponseTo> editors = await _editorService.GetAllEditors();

                return Ok(editors);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error getting all editors");
                return StatusCode(500, "An error occurred while retrieving editors");
            }
        }

        [HttpGet("{id:long}")]
        [ProducesResponseType(typeof(EditorResponseTo), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<EditorResponseTo>> GetEditorById([FromRoute] long id)
        {
            try
            {
                _logger.LogInformation("Getting editor by id: {Id}", id);

                var getEditorRequest = new EditorRequestTo { Id = id };
                EditorResponseTo editor = await _editorService.GetEditor(getEditorRequest);

                return Ok(editor);
            }
            catch (NewNotFoundException ex)
            {
                _logger.LogWarning(ex, "Editor not found with id: {Id}", id);
                return NotFound($"Editor with id {id} not found");
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error getting editor by id: {Id}", id);
                return StatusCode(500, "An error occurred while retrieving the editor");
            }
        }

        [ProducesResponseType(typeof(EditorResponseTo), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<EditorResponseTo>> UpdateNews(
            [FromBody] EditorRequestTo updateEditorRequest)
        {
            try
            {
                _logger.LogInformation("Updating editor with id: {Id}", updateEditorRequest.Id);

                var updatedEditor = await _editorService.UpdateEditor(updateEditorRequest);

                return Ok(updatedEditor);
            }
            catch (NewNotFoundException ex)
            {
                _logger.LogWarning(ex, "Editor not found for update with id: {Id}", updateEditorRequest.Id);
                return NotFound($"Editor with id {updateEditorRequest.Id} not found");
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error updating editor with id: {Id}", updateEditorRequest.Id);
                return StatusCode(500, "An error occurred while updating the editor");
            }
        }

        [HttpDelete("{id:long}")]
        [ProducesResponseType(StatusCodes.Status204NoContent)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<IActionResult> DeleteEditor([FromRoute] long id)
        {
            try
            {
                _logger.LogInformation("Deleting editor with id: {Id}", id);

                var deleteEditorRequest = new EditorRequestTo { Id = id };
                await _editorService.DeleteEditor(deleteEditorRequest);

                return NoContent();
            }
            catch (EditorNotFoundException ex)
            {
                _logger.LogWarning(ex, "Editor not found for deletion with id: {Id}", id);
                return NotFound();
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error deleting editor with id: {Id}", id);
                return StatusCode(500, "An error occurred while deleting the editor");
            }
        }
    }
}
