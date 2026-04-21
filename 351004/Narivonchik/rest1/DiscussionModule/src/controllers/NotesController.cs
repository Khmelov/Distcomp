using DiscussionModule.DTOs.requests;
using DiscussionModule.DTOs.responses;
using DiscussionModule.interfaces;
using Microsoft.AspNetCore.Mvc;

namespace DiscussionModule.controllers;

[ApiController]
[Route("api/v1.0/[controller]")]
public class NotesController : ControllerBase
{
    private readonly INoteService _service;

    public NotesController(INoteService service)
    {
        _service = service;
    }

    [HttpPost]
    public async Task<ActionResult<NoteResponseTo>> CreateNote([FromBody] NoteRequestTo note)
    {
        var created = await _service.CreateNote(note);
        return CreatedAtAction(nameof(GetNoteById), new { id = created.Id }, created);
    }

    [HttpGet]
    public async Task<ActionResult<IEnumerable<NoteResponseTo>>> GetAllNotes()
    {
        try
        {
            var notes = await _service.GetAllNotes();
            return Ok(notes);
        }
        catch(Exception)
        {
            return Ok();
        }
    }

    [HttpGet("{id:long}")]
    public async Task<ActionResult<NoteResponseTo>> GetNoteById(long id)
    {
        var dto = new NoteRequestTo() { Id = id };
        var post = await _service.GetNote(dto);
        if (post == null)
            return NotFound();

        return Ok(post);
    }

    [HttpPut("{id:long}")]
    public async Task<ActionResult<NoteResponseTo>> UpdateNote(long id, [FromBody] NoteRequestTo note)
    {
        note.Id = id;
        var updated = await _service.UpdateNote(note);
        if (updated == null)
            return NotFound();

        return Ok(updated);
    }

    [HttpDelete("{id:long}")]
    public async Task<IActionResult> DeleteNote(long id)
    {
        var dto = new NoteRequestTo() { Id = id };
        await _service.DeleteNote(dto);

        return NoContent();
    }
}