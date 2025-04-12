using Microsoft.AspNetCore.Mvc;
using System.Net;

[ApiController]
[Route("api/v1.0/editors")]
public class EditorsController : ControllerBase
{
    private readonly IEditorService _editorService;

    public EditorsController(IEditorService editorService, ILogger<EditorsController> logger)
    {
        _editorService = editorService;
    }

    [HttpPost]
    public async Task<IActionResult> CreateEditor([FromBody] EditorRequestTo editorDto)
    {
        try
        {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            var createdEditor = await _editorService.CreateEditorAsync(editorDto);
            return CreatedAtAction(nameof(GetEditorById), new { id = createdEditor.Id }, createdEditor);
        }
        catch (ArgumentException ex)
        {
            return BadRequest(new { error = ex.Message });
        }
        catch (InvalidOperationException ex)
        {
            return StatusCode(StatusCodes.Status403Forbidden, new { error = "Account already exists" });
        }
        catch (Exception ex)
        {
            return StatusCode(StatusCodes.Status500InternalServerError, new { error = "An error occurred while creating the editor." });
        }
    }

    [HttpGet("{id}")]
    public async Task<IActionResult> GetEditorById(int id)
    {
        try
        {
            var editor = await _editorService.GetEditorByIdAsync(id);
            if (editor == null)
            {
                return NotFound();
            }
            return Ok(editor);
        }
        catch (Exception ex)
        {
            return StatusCode(StatusCodes.Status500InternalServerError, "An error occurred while retrieving the editor.");
        }
    }

    [HttpGet]
    public async Task<IActionResult> GetAllEditors()
    {
        try
        {
            var editors = await _editorService.GetAllEditorsAsync();
            return Ok(editors);
        }
        catch (Exception ex)
        {
            return StatusCode(StatusCodes.Status500InternalServerError, "An error occurred while retrieving editors.");
        }
    }

    [HttpPut]
    public async Task<IActionResult> UpdateEditor([FromBody] Editor editorDto)
    {
        try
        {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            var updatedEditor = await _editorService.UpdateEditorAsync(editorDto);
            if (updatedEditor == null)
            {
                return NotFound();
            }
            return Ok(updatedEditor);
        }
        catch (Exception ex)
        {
            return StatusCode(StatusCodes.Status500InternalServerError, "An error occurred while updating the editor.");
        }
    }

    [HttpDelete("{id}")]
    public async Task<IActionResult> DeleteEditor(int id)
    {
        try
        {
            bool deleted = await _editorService.DeleteEditorAsync(id);
            if (!deleted)
            {
                return NotFound();
            }
            return NoContent(); 
        }
        catch (Exception ex)
        {
            return StatusCode(StatusCodes.Status500InternalServerError, "An error occurred while deleting the editor.");
        }
    }
}
