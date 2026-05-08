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
        private readonly IHttpClientFactory _httpClientFactory;
        private readonly ILogger<NotesController> _logger;

        public NotesController(
            IHttpClientFactory httpClientFactory,
            ILogger<NotesController> logger)
        {
            _httpClientFactory = httpClientFactory;
            _logger = logger;
        }
        
        private HttpClient CreateClient()
        {
            return _httpClientFactory.CreateClient("discussion");
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
                // make http request instead of request to NoteService
                _logger.LogInformation("Forwarding create note request");

                var client = CreateClient();

                var response = await client.PostAsJsonAsync("/api/v1.0/notes", createNoteRequest);

                if (!response.IsSuccessStatusCode)
                {
                    return StatusCode((int)response.StatusCode);
                }

                var createdNote = await response.Content.ReadFromJsonAsync<NoteResponseTo>();
                
                // get created note using http request
                return CreatedAtAction(
                    nameof(GetNoteById),
                    new { id = createdNote!.Id },
                    createdNote
                );
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error creating note");
                return StatusCode(500);
            }
        }

        [HttpGet]
        [ProducesResponseType(typeof(IEnumerable<NoteResponseTo>), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<IEnumerable<NoteResponseTo>>> GetAllNotes()
        {
            try
            {
                var client = CreateClient();

                var notes = await client.GetFromJsonAsync<IEnumerable<NoteResponseTo>>("/api/v1.0/notes");

                return Ok(notes);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error getting all notes");
                return StatusCode(500);
            }
        }

        [HttpGet("{id:long}")]
        [ProducesResponseType(typeof(NoteResponseTo), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<NoteResponseTo>> GetNoteById(long id)
        {
            try
            {
                _logger.LogInformation("Forwarding get note by {Id}", id);

                var client = CreateClient();

                var response = await client.GetAsync($"/api/v1.0/notes/{id}");

                if (response.StatusCode == System.Net.HttpStatusCode.NotFound)
                {
                    return NotFound();
                }

                var note = await response.Content.ReadFromJsonAsync<NoteResponseTo>();

                return Ok(note);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error getting note");
                return StatusCode(500);
            }
        }

        [HttpPut("{id:long}")]
        [ProducesResponseType(typeof(NoteResponseTo), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<NoteResponseTo>> UpdateNote( long id,
            [FromBody] NoteRequestTo updateNoteRequest)
        {
            try
            {
                _logger.LogInformation("Forwarding update post {Id}", id);
                var client = CreateClient();
                
                var response = await client.PutAsJsonAsync(
                    $"/api/v1.0/notes/{id}",
                    updateNoteRequest);

                if (response.StatusCode == System.Net.HttpStatusCode.NotFound)
                {
                    return NotFound();
                }

                var post = await response.Content.ReadFromJsonAsync<NoteResponseTo>();

                return Ok(post);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error updating post");
                return StatusCode(500);
            }
        }

        [HttpDelete("{id:long}")]
        [ProducesResponseType(StatusCodes.Status204NoContent)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<IActionResult> DeleteNote(long id)
        {
            try
            {
                _logger.LogInformation("Forwarding delete post {Id}", id);

                var client = CreateClient();

                var deleteNoteRequest = new NoteRequestTo { Id = id };
                var response = await client.DeleteAsync($"/api/v1.0/notes/{id}");

                if (response.StatusCode == System.Net.HttpStatusCode.NotFound)
                {
                    return NotFound();
                }

                return NoContent();
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error deleting post");
                return StatusCode(500);
            }
        }
    }