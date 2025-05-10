using Microsoft.AspNetCore.Mvc;
using WebApplication1.DTO;
using WebApplication1.Repository;
using WebApplication1.Service;

namespace WebApplication1.Controller
{
    [ApiController]
    [Route("api/v1.0/editors")]
    public class EditorController : ControllerBase
    {
        private readonly IEditorService _editorService;
        public EditorController(IEditorService editorService) => _editorService = editorService;

        [HttpGet]
        public async Task<ActionResult<IEnumerable<EditorResponseTo>>> GetAll()
        {
            var allEditors = await _editorService.GetAllEditorsAsync(1, 1000);
            return Ok(allEditors.Items);
        }

        [HttpGet("{id:long}")]
        public async Task<ActionResult<EditorResponseTo>> GetById(long id)
        {
            var editor = await _editorService.GetEditorByIdAsync(id);
            return Ok(editor);
        }

        [HttpPost]
        public async Task<ActionResult<EditorResponseTo>> Create([FromBody] EditorRequestTo dto)
        {
            var created = await _editorService.CreateEditorAsync(dto);
            return CreatedAtAction(nameof(GetById), new { id = created.id }, created);
        }

        [HttpPut]
        public async Task<ActionResult<EditorResponseTo>> Update([FromBody] EditorRequestTo dto)
        {
            if (dto.Id == null)
                return BadRequest("Id must be provided in the request body");
            var updated = await _editorService.UpdateEditorAsync(dto.Id.Value, dto);
            return Ok(updated);
        }

        [HttpDelete("{id:long}")]
        public async Task<IActionResult> Delete(long id)
        {
            try
            {
                await _editorService.DeleteEditorAsync(id);
                return NoContent();
            }
            catch (ValidationException ex)
            {
                return StatusCode(ex.HttpCode, new { error = ex.Message, code = ex.ErrorCode });
            }
        }
    }
}
