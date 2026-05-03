using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using rest1.application.DTOs.requests;
using rest1.application.DTOs.responses;
using rest1.application.exceptions;
using rest1.application.interfaces.services;

namespace rest1.api.controllers.v2;

[ApiController]
[Route("api/v2.0/notes")]
[Authorize(AuthenticationSchemes = "Bearer")]
public class NotesV2Controller : ControllerBase
{
    private readonly IHttpClientFactory _httpClientFactory;
    private readonly INewsService _newsService;
    private readonly ICreatorService _creatorService;
    private readonly ILogger<NotesV2Controller> _logger;

    public NotesV2Controller(
        IHttpClientFactory httpClientFactory,
        INewsService newsService,
        ICreatorService creatorService,
        ILogger<NotesV2Controller> logger)
    {
        _httpClientFactory = httpClientFactory;
        _newsService = newsService;
        _creatorService = creatorService;
        _logger = logger;
    }

    private HttpClient CreateClient()
    {
        return _httpClientFactory.CreateClient("discussion");
    }

    private async Task<bool> CanModifyNote(long newsId, string currentUserLogin)
    {
        var news = await _newsService.GetNews(new NewsRequestTo { Id = newsId });
        var creator = await _creatorService.GetCreator(new CreatorRequestTo { Id = news.CreatorId });
        return creator.Login == currentUserLogin;
    }

    [HttpGet]
    [AllowAnonymous]
    [ProducesResponseType(typeof(IEnumerable<NoteResponseTo>), StatusCodes.Status200OK)]
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
            return StatusCode(500, new { errorMessage = "An error occurred while retrieving notes", errorCode = "50011" });
        }
    }

    [HttpPost]
    [ProducesResponseType(typeof(NoteResponseTo), StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status403Forbidden)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<ActionResult<NoteResponseTo>> CreateNote([FromBody] NoteRequestTo createNoteRequest)
    {
        try
        {
            _logger.LogInformation("Creating note for newsId: {NewsId}", createNoteRequest.NewsId);

            var isAdmin = User.IsInRole("ADMIN");
            var currentUserLogin = User.FindFirst(System.IdentityModel.Tokens.Jwt.JwtRegisteredClaimNames.Sub)?.Value;

            if (!isAdmin && !await CanModifyNote(createNoteRequest.NewsId, currentUserLogin!))
            {
                return StatusCode(403, new { errorMessage = "Access denied", errorCode = "40301" });
            }

            var client = CreateClient();
            var response = await client.PostAsJsonAsync("/api/v1.0/notes", createNoteRequest);

            if (!response.IsSuccessStatusCode)
            {
                return StatusCode((int)response.StatusCode);
            }

            var createdNote = await response.Content.ReadFromJsonAsync<NoteResponseTo>();

            return CreatedAtAction(nameof(GetNoteById), new { id = createdNote!.Id }, createdNote);
        }
        catch (NewNotFoundException ex)
        {
            return NotFound(new { errorMessage = ex.Message, errorCode = "40409" });
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error creating note");
            return StatusCode(500, new { errorMessage = "An error occurred while creating the note", errorCode = "50012" });
        }
    }

    [HttpGet("{id:long}")]
    [AllowAnonymous]
    [ProducesResponseType(typeof(NoteResponseTo), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<ActionResult<NoteResponseTo>> GetNoteById(long id)
    {
        try
        {
            _logger.LogInformation("Getting note by id: {Id}", id);

            var client = CreateClient();
            var response = await client.GetAsync($"/api/v1.0/notes/{id}");

            if (response.StatusCode == System.Net.HttpStatusCode.NotFound)
            {
                return NotFound(new { errorMessage = $"Note with id {id} not found", errorCode = "40410" });
            }

            var note = await response.Content.ReadFromJsonAsync<NoteResponseTo>();
            return Ok(note);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error getting note");
            return StatusCode(500, new { errorMessage = "An error occurred while retrieving the note", errorCode = "50013" });
        }
    }

    [HttpPut("{id:long}")]
    [ProducesResponseType(typeof(NoteResponseTo), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status403Forbidden)]
    public async Task<ActionResult<NoteResponseTo>> UpdateNote(long id, [FromBody] NoteRequestTo updateNoteRequest)
    {
        try
        {
            _logger.LogInformation("Updating note {Id}", id);

            var isAdmin = User.IsInRole("ADMIN");
            var currentUserLogin = User.FindFirst(System.IdentityModel.Tokens.Jwt.JwtRegisteredClaimNames.Sub)?.Value;

            if (!isAdmin && !await CanModifyNote(updateNoteRequest.NewsId, currentUserLogin!))
            {
                return StatusCode(403, new { errorMessage = "Access denied", errorCode = "40302" });
            }

            var client = CreateClient();
            var response = await client.PutAsJsonAsync($"/api/v1.0/notes/{id}", updateNoteRequest);

            if (response.StatusCode == System.Net.HttpStatusCode.NotFound)
            {
                return NotFound(new { errorMessage = $"Note with id {id} not found", errorCode = "40411" });
            }

            var note = await response.Content.ReadFromJsonAsync<NoteResponseTo>();
            return Ok(note);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error updating note");
            return StatusCode(500, new { errorMessage = "An error occurred while updating the note", errorCode = "50014" });
        }
    }

    [HttpDelete("{id:long}")]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status403Forbidden)]
    public async Task<IActionResult> DeleteNote(long id)
    {
        try
        {
            _logger.LogInformation("Deleting note {Id}", id);

            // First get the note to check ownership
            var client = CreateClient();
            var getResponse = await client.GetAsync($"/api/v1.0/notes/{id}");
            
            if (getResponse.StatusCode == System.Net.HttpStatusCode.NotFound)
            {
                return NotFound(new { errorMessage = $"Note with id {id} not found", errorCode = "40412" });
            }

            var note = await getResponse.Content.ReadFromJsonAsync<NoteResponseTo>();
            
            var isAdmin = User.IsInRole("ADMIN");
            var currentUserLogin = User.FindFirst(System.IdentityModel.Tokens.Jwt.JwtRegisteredClaimNames.Sub)?.Value;

            if (!isAdmin && !await CanModifyNote(note!.NewsId, currentUserLogin!))
            {
                return StatusCode(403, new { errorMessage = "Access denied", errorCode = "40303" });
            }

            var deleteResponse = await client.DeleteAsync($"/api/v1.0/notes/{id}");
            
            if (deleteResponse.StatusCode == System.Net.HttpStatusCode.NotFound)
            {
                return NotFound(new { errorMessage = $"Note with id {id} not found", errorCode = "40413" });
            }

            return NoContent();
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error deleting note");
            return StatusCode(500, new { errorMessage = "An error occurred while deleting the note", errorCode = "50015" });
        }
    }
}