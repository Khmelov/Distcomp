using Microsoft.AspNetCore.Mvc;

[ApiController]
[Route("api/v1.0/notes")]
public class NotesController : ControllerBase
{
    private readonly INoteService _noteService;

    public NotesController(INoteService noteService, ILogger<NotesController> logger)
    {
        _noteService = noteService;
    }

    [HttpPost]
    public async Task<IActionResult> CreateNote([FromBody] NoteRequestTo noteDto)
    {
        try
        {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            var createdNote = await _noteService.CreateNoteAsync(noteDto);
            return CreatedAtAction(nameof(GetNoteById), new { id = createdNote.Id }, createdNote);
        }
        catch (KeyNotFoundException ex)
        {
            return BadRequest(new { error = ex.Message });
        }
        catch (ArgumentException ex) 
        { 
            return BadRequest(new { error = ex.Message });
        }
        catch (Exception ex)
        {
            return StatusCode(StatusCodes.Status500InternalServerError, new { error = "An error occurred while creating the note." });
        }
    }

    [HttpGet("{id}")]
    public async Task<IActionResult> GetNoteById(int id)
    {
        try
        {
            var note = await _noteService.GetNoteByIdAsync(id);
            if (note == null)
            {
                return NotFound();
            }
            return Ok(note);
        }
        catch (Exception ex)
        {
            return StatusCode(StatusCodes.Status500InternalServerError, "An error occurred while retrieving the Note.");
        }
    }

    [HttpGet]
    public async Task<IActionResult> GetAllNotes()
    {
        try
        {
            var note = await _noteService.GetAllNotesAsync();
            return Ok(note);
        }
        catch (Exception ex)
        {
            return StatusCode(StatusCodes.Status500InternalServerError, "An error occurred while retrieving Note.");
        }
    }

    [HttpPut]
    public async Task<IActionResult> UpdateNote([FromBody] Note noteDto)
    {
        try
        {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            var updatedNote = await _noteService.UpdateNoteAsync(noteDto);
            if (updatedNote == null)
            {
                return NotFound();
            }
            return Ok(updatedNote);
        }
        catch (Exception ex)
        {
            return StatusCode(StatusCodes.Status500InternalServerError, "An error occurred while updating the Note.");
        }
    }

    [HttpDelete("{id}")]
    public async Task<IActionResult> DeleteNote(int id)
    {
        try
        {
            bool deleted = await _noteService.DeleteNoteAsync(id);
            if (!deleted)
            {
                return NotFound();
            }
            return NoContent();
        }
        catch (Exception ex)
        {
            return StatusCode(StatusCodes.Status500InternalServerError, "An error occurred while deleting the Note.");
        }
    }
}
