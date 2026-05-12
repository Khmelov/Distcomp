using Discussion.src.NewsPortal.Discussion.Application.Dtos.ResponseTo;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Publisher.src.NewsPortal.Publisher.Application.Dtos;
using Publisher.src.NewsPortal.Publisher.Application.Dtos.RequestTo;
using Publisher.src.NewsPortal.Publisher.Application.Services.Abstractions;
using Publisher.src.NewsPortal.Publisher.Domain.Entities;
using Publisher.src.NewsPortal.Publisher.Infrastructure.Caching;
using Publisher.src.NewsPortal.Publisher.Infrastructure.Messaging;
using System.Security.Claims;

namespace Publisher.src.NewsPortal.Publisher.API.Controllers.v2;

[ApiController]
[Route("api/v2.0/notes")]
[Authorize]
public class NotesControllerV2 : ControllerBase
{
    private readonly IKafkaProducerService _kafkaProducer;
    private readonly ILogger<NotesControllerV2> _logger;
    private readonly KafkaConsumerService _kafkaConsumerService;
    private readonly INewsValidationService _newsValidationService;
    private readonly IRedisCacheService _redisCache;
    private readonly ICreatorService _creatorService;

    private const string CACHE_KEY_NOTES_ALL = "notes:all:v2";
    private const string CACHE_KEY_NOTE_PREFIX = "note:v2:";

    public NotesControllerV2(
        IKafkaProducerService kafkaProducer,
        ILogger<NotesControllerV2> logger,
        KafkaConsumerService kafkaConsumerService,
        INewsValidationService newsValidationService,
        IRedisCacheService redisCache,
        ICreatorService creatorService)
    {
        _kafkaProducer = kafkaProducer;
        _logger = logger;
        _kafkaConsumerService = kafkaConsumerService;
        _newsValidationService = newsValidationService;
        _redisCache = redisCache;
        _creatorService = creatorService;
    }

    private async Task<string?> GetCurrentUserLogin()
    {
        return User.Identity?.Name;
    }

    private async Task<string?> GetCurrentUserRole()
    {
        return User.Claims.FirstOrDefault(c => c.Type == ClaimTypes.Role)?.Value;
    }

    [HttpGet]
    [AllowAnonymous]
    [ProducesResponseType(typeof(IEnumerable<NoteResponseTo>), StatusCodes.Status200OK)]
    public async Task<ActionResult<IEnumerable<NoteResponseTo>>> GetAllNotes()
    {
        var localNotes = _kafkaConsumerService.GetAllNotes().ToList();
        if (localNotes.Any())
        {
            _logger.LogDebug("GET all v2: {Count} notes from memory", localNotes.Count);
            return Ok(localNotes);
        }

        var areExist = await _redisCache.ExistsAsync(CACHE_KEY_NOTES_ALL);
        if (areExist)
        {
            var redisNotes = await _redisCache.GetAsync<IEnumerable<NoteResponseTo>>(CACHE_KEY_NOTES_ALL);
            if (redisNotes != null && redisNotes.Any())
            {
                _ = Task.Run(() =>
                {
                    foreach (var note in redisNotes)
                    {
                        _kafkaConsumerService.AddOrUpdateNote(note);
                    }
                });
                _logger.LogDebug("GET all v2: {Count} notes from Redis", redisNotes.Count());
                return Ok(redisNotes);
            }
        }
        else
        {
            _logger.LogInformation("No notes in cache v2, sending GET_ALL request to Kafka");

            var requestMessage = new KafkaNoteMessage
            {
                Action = "GET_ALL",
                Data = new Note { Id = 0 }
            };

            await _kafkaProducer.SendAsync("InTopic", requestMessage);

            using var cts = new CancellationTokenSource(TimeSpan.FromSeconds(5));
            var timeout = TimeSpan.FromSeconds(5);
            var startTime = DateTime.UtcNow;

            while (DateTime.UtcNow - startTime < timeout)
            {
                var notesFromKafka = _kafkaConsumerService.GetAllNotes().ToList();
                if (notesFromKafka.Any())
                {
                    _ = _redisCache.SetAsync(CACHE_KEY_NOTES_ALL, notesFromKafka, TimeSpan.FromMinutes(5));
                    _logger.LogDebug("GET all v2: {Count} notes from Kafka response", notesFromKafka.Count);
                    return Ok(notesFromKafka);
                }
                await Task.Delay(50, cts.Token);
            }

            _logger.LogWarning("GET_ALL request timeout v2, returning empty list");
            return Ok(new List<NoteResponseTo>());
        }

        return Ok(new List<NoteResponseTo>());
    }

    [HttpGet("{id}")]
    [AllowAnonymous]
    [ProducesResponseType(typeof(NoteResponseTo), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<ActionResult<NoteResponseTo>> GetNoteById(long id)
    {
        var localNote = _kafkaConsumerService.GetNoteById(id);
        if (localNote != null)
        {
            _logger.LogDebug("GET by id v2 {Id} from memory", id);
            return Ok(localNote);
        }

        var cacheKey = $"{CACHE_KEY_NOTE_PREFIX}{id}";
        var isExist = await _redisCache.ExistsAsync(cacheKey);
        if (isExist)
        {
            var redisNote = await _redisCache.GetAsync<NoteResponseTo>(cacheKey);
            if (redisNote != null)
            {
                _ = Task.Run(() => _kafkaConsumerService.AddOrUpdateNote(redisNote));
                _logger.LogDebug("GET by id v2 {Id} from Redis", id);
                return Ok(redisNote);
            }
        }
        else
        {
            _logger.LogInformation("Note {Id} not in cache v2, sending GET request to Kafka", id);

            var requestMessage = new KafkaNoteMessage
            {
                Action = "GET",
                Data = new Note { Id = id }
            };

            await _kafkaProducer.SendAsync("InTopic", requestMessage);

            using var cts = new CancellationTokenSource(TimeSpan.FromSeconds(5));
            var timeout = TimeSpan.FromSeconds(5);
            var startTime = DateTime.UtcNow;

            while (DateTime.UtcNow - startTime < timeout)
            {
                var noteFromKafka = _kafkaConsumerService.GetNoteById(id);
                if (noteFromKafka != null)
                {
                    _ = _redisCache.SetAsync(cacheKey, noteFromKafka, TimeSpan.FromMinutes(10));
                    _logger.LogDebug("GET by id v2 {Id} from Kafka response", id);
                    return Ok(noteFromKafka);
                }
                await Task.Delay(50, cts.Token);
            }

            _logger.LogWarning("GET request for note {Id} timed out v2", id);
            return NotFound(new { errorMessage = $"Note with ID {id} not found", errorCode = "40401" });
        }

        return NotFound(new { errorMessage = $"Note with ID {id} not found", errorCode = "40401" });
    }

    [HttpPost]
    [Authorize(Roles = "ADMIN,CUSTOMER")]
    [ProducesResponseType(typeof(NoteResponseTo), StatusCodes.Status201Created)]
    [ProducesResponseType(typeof(ValidationProblemDetails), StatusCodes.Status400BadRequest)]
    [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status403Forbidden)]
    public async Task<ActionResult<NoteResponseTo>> CreateNote([FromBody] NoteRequestTo noteRequest)
    {
        if (!ModelState.IsValid) return BadRequest(ModelState);

        var newsExists = await _newsValidationService.NewsExistsAsync(noteRequest.NewsId);
        if (!newsExists)
        {
            return NotFound(new { errorMessage = $"News with ID {noteRequest.NewsId} does not exist", errorCode = "40401" });
        }

        var generatedId = DateTime.UtcNow.Ticks;

        var kafkaMessage = new KafkaNoteMessage
        {
            Action = "CREATE",
            Data = new Note
            {
                Id = generatedId,
                NewsId = noteRequest.NewsId,
                Content = noteRequest.Content,
                State = "PENDING"
            }
        };

        await _kafkaProducer.SendAsync("InTopic", kafkaMessage);

        var response = new NoteResponseTo
        {
            Id = generatedId,
            NewsId = noteRequest.NewsId,
            Content = noteRequest.Content,
            State = "PENDING"
        };

        _kafkaConsumerService.AddOrUpdateNote(response);
        _ = _redisCache.SetAsync($"{CACHE_KEY_NOTE_PREFIX}{generatedId}", response, TimeSpan.FromMinutes(10));
        _ = _redisCache.RemoveAsync(CACHE_KEY_NOTES_ALL);
        _ = _redisCache.RemoveAsync("notes:all");

        return CreatedAtAction(nameof(GetNoteById), new { id = generatedId }, response);
    }

    [HttpPut("{id}")]
    [Authorize(Roles = "ADMIN,CUSTOMER")]
    [ProducesResponseType(typeof(NoteResponseTo), StatusCodes.Status200OK)]
    [ProducesResponseType(typeof(ValidationProblemDetails), StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status403Forbidden)]
    public async Task<ActionResult<NoteResponseTo>> UpdateNote(long id, [FromBody] NoteRequestTo noteRequest)
    {
        var existingNote = _kafkaConsumerService.GetNoteById(id);
        if (existingNote == null)
            return NotFound();

        if (!ModelState.IsValid) return BadRequest(ModelState);

        var newsExists = await _newsValidationService.NewsExistsAsync(noteRequest.NewsId);
        if (!newsExists)
        {
            return NotFound(new { errorMessage = $"News with ID {noteRequest.NewsId} does not exist", errorCode = "40401" });
        }

        var optimisticResponse = new NoteResponseTo
        {
            Id = id,
            NewsId = noteRequest.NewsId,
            Content = noteRequest.Content,
            State = existingNote.State
        };

        _kafkaConsumerService.AddOrUpdateNote(optimisticResponse);

        var kafkaMessage = new KafkaNoteMessage
        {
            Action = "UPDATE",
            Data = new Note
            {
                Id = id,
                NewsId = noteRequest.NewsId,
                Content = noteRequest.Content,
                State = existingNote.State
            }
        };

        await _kafkaProducer.SendAsync("InTopic", kafkaMessage);

        _ = _redisCache.SetAsync($"{CACHE_KEY_NOTE_PREFIX}{id}", optimisticResponse, TimeSpan.FromMinutes(10));
        _ = _redisCache.RemoveAsync(CACHE_KEY_NOTES_ALL);
        _ = _redisCache.RemoveAsync("notes:all");

        return Ok(optimisticResponse);
    }

    [HttpDelete("{id}")]
    [Authorize(Roles = "ADMIN")]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status403Forbidden)]
    public async Task<IActionResult> DeleteNote(long id)
    {
        var existingNote = _kafkaConsumerService.GetNoteById(id);
        if (existingNote == null)
            return NotFound();

        var kafkaMessage = new KafkaNoteMessage
        {
            Action = "DELETE",
            Data = new Note
            {
                Id = id,
                NewsId = existingNote.NewsId
            }
        };

        await _kafkaProducer.SendAsync("InTopic", kafkaMessage);

        _kafkaConsumerService.RemoveNote(id);
        _ = _redisCache.RemoveAsync($"{CACHE_KEY_NOTE_PREFIX}{id}");
        _ = _redisCache.RemoveAsync(CACHE_KEY_NOTES_ALL);
        _ = _redisCache.RemoveAsync("notes:all"); 

        return NoContent();
    }
}