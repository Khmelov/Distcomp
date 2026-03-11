using Microsoft.AspNetCore.Mvc;
using rest1.application.DTOs.requests;
using rest1.application.DTOs.responses;
using rest1.application.exceptions;
using rest1.application.interfaces.services;

namespace rest1.api.controllers;

    [ApiController]
    [Route("api/v1.0/[controller]")]
    public class NotesController : ControllerBase
    {
        private readonly INoteService _noteService;
        private readonly ILogger<NotesController> _logger;

        public NotesController(INoteService noteService, ILogger<NotesController> logger)
        {
            _noteService = noteService;
            _logger = logger;
        }

        [HttpPost]
        [ProducesResponseType(typeof(NoteResponseTo), StatusCodes.Status201Created)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<NoteResponseTo>> CreateNote(
            [FromBody] NoteRequestTo createNoteRequest)
        {
            try
            {
                _logger.LogInformation("Creating note {request}", createNoteRequest);

                NoteResponseTo createdNote =
                    await _noteService.CreateNote(createNoteRequest);

                return CreatedAtAction(
                    nameof(CreateNote),
                    new { id = createdNote.Id },
                    createdNote
                );
            }
            catch (NoteAlreadyExistsException ex)
            {
                _logger.LogError(ex, "Post already exists");
                return StatusCode(403);
            }
            catch (ReferenceException)
            {
                return StatusCode(404);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error creating note");
                return StatusCode(500, "An error occurred while creating the note");
            }
        }

        [HttpGet]
        [ProducesResponseType(typeof(IEnumerable<NoteResponseTo>), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<IEnumerable<NoteResponseTo>>> GetAllNotes()
        {
            try
            {
                _logger.LogInformation("Getting all notes");

                IEnumerable<NoteResponseTo> notes =
                    await _noteService.GetAllNotes();

                return Ok(notes);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error getting all notes");
                return StatusCode(500, "An error occurred while retrieving notes");
            }
        }

        [HttpGet("{id:long}")]
        [ProducesResponseType(typeof(NoteResponseTo), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<NoteResponseTo>> GetNoteById([FromRoute] long id)
        {
            try
            {
                _logger.LogInformation("Getting note by id: {Id}", id);

                var getNoteRequest = new NoteRequestTo { Id = id };
                NoteResponseTo note =
                    await _noteService.GetNote(getNoteRequest);

                return Ok(note);
            }
            catch (NoteNotFoundException ex)
            {
                _logger.LogWarning(ex, "Note not found with id: {Id}", id);
                return NotFound($"Note with id {id} not found");
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error getting note by id: {Id}", id);
                return StatusCode(500, "An error occurred while retrieving the note");
            }
        }

        [HttpPut]
        [ProducesResponseType(typeof(NoteResponseTo), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<NoteResponseTo>> UpdateNote(
            [FromBody] NoteRequestTo updateNoteRequest)
        {
            try
            {
                _logger.LogInformation("Updating note with id: {Id}", updateNoteRequest.Id);

                var updatedNote =
                    await _noteService.UpdateNote(updateNoteRequest);

                return Ok(updatedNote);
            }
            catch (NoteNotFoundException ex)
            {
                _logger.LogWarning(
                    ex,
                    "Note not found for update with id: {Id}",
                    updateNoteRequest.Id);

                return NotFound(
                    $"Note with id {updateNoteRequest.Id} not found");
            }
            catch (Exception ex)
            {
                _logger.LogError(
                    ex,
                    "Error updating note with id: {Id}",
                    updateNoteRequest.Id);

                return StatusCode(
                    500,
                    "An error occurred while updating the note");
            }
        }

        [HttpDelete("{id:long}")]
        [ProducesResponseType(StatusCodes.Status204NoContent)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<IActionResult> DeleteNote([FromRoute] long id)
        {
            try
            {
                _logger.LogInformation("Deleting note with id: {Id}", id);

                var deleteNoteRequest = new NoteRequestTo { Id = id };
                await _noteService.DeleteNote(deleteNoteRequest);

                return NoContent();
            }
            catch (NoteNotFoundException ex)
            {
                _logger.LogWarning(ex, "Note not found for deletion with id: {Id}", id);
                return NotFound();
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error deleting note with id: {Id}", id);
                return StatusCode(500, "An error occurred while deleting the note");
            }
        }
    }