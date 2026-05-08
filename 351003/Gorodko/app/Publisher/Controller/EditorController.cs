using Microsoft.AspNetCore.Mvc;
using Publisher.Dto;
using Publisher.Exceptions;
using Publisher.Service;

namespace Publisher.Controller {
    [ApiController]
    [Route("api/v1.0/editors")]
    public class EditorController : BaseController<EditorRequestTo, EditorResponseTo> {
        private readonly EditorService _editorService;

        public EditorController(EditorService editorService, ILogger<EditorController> logger)
            : base(logger) {
            _editorService = editorService;
        }

        [HttpGet]
        [ProducesResponseType(typeof(IEnumerable<EditorResponseTo>), StatusCodes.Status200OK)]
        public async Task<ActionResult<IEnumerable<EditorResponseTo>>> GetEditors() {
            var editors = await _editorService.GetAllAsync();
            return Ok(editors);
        }

        [HttpGet("{id:long}")]
        [ProducesResponseType(typeof(EditorResponseTo), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<ActionResult<EditorResponseTo>> GetEditor(long id) {
            var editor = await _editorService.GetByIdAsync(id);
            return editor == null ? NotFound() : Ok(editor);
        }

        [HttpPut]
        [ProducesResponseType(typeof(EditorResponseTo), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<ActionResult<EditorResponseTo>> UpdateEditor([FromBody] EditorRequestTo request) {
            _logger.LogInformation($"PUT request received for editor ID: {request?.Id}");

            if (!ModelState.IsValid) {
                _logger.LogWarning($"Invalid model state for editor update");
                return BadRequest(ModelState);
            }

            if (request == null || request.Id <= 0) {
                _logger.LogWarning($"Invalid request data");
                return BadRequest("Invalid request data");
            }

            try {
                var updatedEditor = await _editorService.UpdateAsync(request);

                if (updatedEditor == null) {
                    _logger.LogWarning($"Editor with ID {request.Id} not found");
                    return NotFound();
                }

                _logger.LogInformation($"Successfully updated editor with ID: {request.Id}");
                return Ok(updatedEditor);
            }
            catch (ValidationException ex) {
                _logger.LogWarning($"Validation error: {ex.Message}");
                return BadRequest(new { error = ex.Message });
            }
            catch (ForbiddenException ex) {
                return CreateErrorResponse(StatusCodes.Status403Forbidden, ex.Message);
            }
            catch (Exception ex) {
                _logger.LogError(ex, $"Error updating editor with ID: {request.Id}");
                return StatusCode(500, new { error = "Internal server error" });
            }
        }

        [HttpPost]
        [ProducesResponseType(typeof(EditorResponseTo), StatusCodes.Status201Created)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        public async Task<ActionResult<EditorResponseTo>> CreateEditor([FromBody] EditorRequestTo request) {
            _logger.LogInformation($"POST request received for new editor");

            if (!ModelState.IsValid) {
                _logger.LogWarning($"Invalid model state for editor creation");
                return BadRequest(ModelState);
            }

            try {
                var editor = await _editorService.AddAsync(request);
                _logger.LogInformation($"Created editor with ID: {editor.Id}");

                return CreatedAtAction(
                    nameof(GetEditor),
                    new { id = editor.Id },
                    editor
                );
            }
            catch (ValidationException ex) {
                _logger.LogWarning($"Validation error: {ex.Message}");
                return BadRequest(new { error = ex.Message });
            }
            catch (ForbiddenException ex) {
                return CreateErrorResponse(StatusCodes.Status403Forbidden, ex.Message);
            }
            catch (Exception ex) {
                _logger.LogError(ex, "Error creating editor");
                return StatusCode(500, new { error = "Internal server error" });
            }
        }

        [HttpPut("{id:long}")]
        [ProducesResponseType(typeof(EditorResponseTo), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<ActionResult<EditorResponseTo>> UpdateEditor(
        long id,
        [FromBody] EditorRequestTo request) {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            request.Id = id;

            var updatedEditor = await _editorService.UpdateAsync(request);
            return updatedEditor == null ? NotFound() : Ok(updatedEditor);
        }

        [HttpDelete("{id:long}")]
        [ProducesResponseType(StatusCodes.Status204NoContent)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<IActionResult> DeleteEditor(long id) {
            var deleted = await _editorService.DeleteAsync(id);
            return deleted ? NoContent() : NotFound();
        }
    }
}