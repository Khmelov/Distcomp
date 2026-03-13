using Microsoft.AspNetCore.Mvc;
using Project.Dto;
using Project.Exceptions;
using Project.Service;

namespace Project.Controller {
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

        [HttpPost]
        [ProducesResponseType(typeof(EditorResponseTo), StatusCodes.Status201Created)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        public async Task<ActionResult<EditorResponseTo>> CreateEditor([FromBody] EditorRequestTo request) {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            try {
                var editor = await _editorService.CreateEditorAsync(request);
                return CreatedAtAction(nameof(GetEditor), new { id = editor.Id }, editor);
            }
            catch (ValidationException ex) {
                return CreateErrorResponse(StatusCodes.Status400BadRequest, ex.Message);
            }
        }

        [HttpPut]
        [ProducesResponseType(typeof(EditorResponseTo), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<ActionResult<EditorResponseTo>> UpdateEditor([FromBody] EditorRequestTo request) {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            var updatedEditor = await _editorService.UpdateAsync(request);
            return updatedEditor == null ? NotFound() : Ok(updatedEditor);
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

            // Устанавливаем ID из маршрута
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