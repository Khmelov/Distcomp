using Microsoft.AspNetCore.Mvc;
using RW.Discussion.DTOs;
using RW.Discussion.Services;

namespace RW.Discussion.Controllers;

[ApiController]
[Route("api/v1.0/notes")]
public class NotesController : ControllerBase
{
    private readonly CassandraNoteService _noteService;

    public NotesController(CassandraNoteService noteService)
    {
        _noteService = noteService;
    }

    [HttpGet]
    public async Task<IActionResult> GetAll()
    {
        var result = await _noteService.GetAllAsync();
        return Ok(result);
    }

    [HttpGet("{id:long}")]
    public async Task<IActionResult> GetById(long id)
    {
        var result = await _noteService.GetByIdAsync(id);
        if (result == null)
            return NotFound(new { errorCode = 404, errorMessage = $"Note with id {id} was not found." });
        return Ok(result);
    }

    [HttpPost]
    public async Task<IActionResult> Create([FromBody] NoteRequestTo dto)
    {
        if (string.IsNullOrEmpty(dto.Content) || dto.Content.Length < 2 || dto.Content.Length > 2048)
            return StatusCode(403, new { errorCode = 403, errorMessage = "Content must be between 2 and 2048 characters." });

        var result = await _noteService.CreateAsync(dto);
        return CreatedAtAction(nameof(GetById), new { id = result.Id }, result);
    }

    [HttpPut]
    public async Task<IActionResult> Update([FromBody] NoteRequestTo dto)
    {
        if (string.IsNullOrEmpty(dto.Content) || dto.Content.Length < 2 || dto.Content.Length > 2048)
            return StatusCode(403, new { errorCode = 403, errorMessage = "Content must be between 2 and 2048 characters." });

        var result = await _noteService.UpdateAsync(dto);
        if (result == null)
            return NotFound(new { errorCode = 404, errorMessage = $"Note with id {dto.Id} was not found." });
        return Ok(result);
    }

    [HttpDelete("{id:long}")]
    public async Task<IActionResult> Delete(long id)
    {
        var deleted = await _noteService.DeleteAsync(id);
        if (!deleted)
            return NotFound(new { errorCode = 404, errorMessage = $"Note with id {id} was not found." });
        return NoContent();
    }
}
