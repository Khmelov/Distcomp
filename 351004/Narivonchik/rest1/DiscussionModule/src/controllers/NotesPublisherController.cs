using System.Text.Json;
using DiscussionModule.DTOs.requests;
using DiscussionModule.DTOs.responses;
using DiscussionModule.kafka;
using Microsoft.AspNetCore.Mvc;
using RedisService.interfaces;

namespace DiscussionModule.controllers;

[ApiController]
[Route("api/v1.0/[controller]")]
public class NotesPublisherController : ControllerBase
{
    private readonly KafkaProducer _producer;
    private readonly IRedisCacheService _cacheService;
    private readonly ILogger<NotesPublisherController> _logger;
    private readonly string _inTopic;
    private readonly string _outTopic;
    private readonly TimeSpan _requestTimeout;
    private readonly string _notesCachePrefix;
    private readonly string _notesListCacheKey;

    public NotesPublisherController(
        KafkaProducer producer,
        IRedisCacheService cacheService,
        IConfiguration configuration,
        ILogger<NotesPublisherController> logger)
    {
        _producer = producer;
        _cacheService = cacheService;
        _logger = logger;
        
        _inTopic = configuration["Kafka:InTopic"] ?? "InTopic";
        _outTopic = configuration["Kafka:OutTopic"] ?? "OutTopic";
        _requestTimeout = TimeSpan.FromMilliseconds(
            configuration.GetValue<int>("Kafka:RequestTimeoutMs", 30000));
        _notesCachePrefix = configuration["Redis:NotesCachePrefix"] ?? "note:";
        _notesListCacheKey = configuration["Redis:NotesListCacheKey"] ?? "notes:all";
    }

    [HttpPost]
    [ProducesResponseType(typeof(NoteResponseTo), StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status500InternalServerError)]
    public async Task<ActionResult<NoteResponseTo>> CreateNote([FromBody] NoteRequestTo note)
    {
        try
        {
            _logger.LogInformation("Publishing CREATE note request");
            
            var response = await PublishAndWaitResponseAsync("CREATE", note);
            
            if (response.Status == "APPROVED" && response.Data != null)
            {
                var createdNote = JsonSerializer.Deserialize<NoteResponseTo>(
                    response.Data.ToString()!);
                
                // Инвалидируем кэш списка
                await _cacheService.RemoveAsync(_notesListCacheKey);
                
                // Кэшируем созданную заметку
                var cacheKey = $"{_notesCachePrefix}{createdNote!.Id}";
                await _cacheService.SetAsync(cacheKey, createdNote);
                
                return CreatedAtAction(nameof(GetNoteById), new { id = createdNote.Id }, createdNote);
            }
            else if (response.Status == "DECLINED")
            {
                return BadRequest(new { Message = "Note was declined by moderation" });
            }
            
            return StatusCode(500, "Failed to create note");
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
            
            // Проверяем кэш
            var cachedNotes = await _cacheService.GetAsync<List<NoteResponseTo>>(_notesListCacheKey);
            
            if (cachedNotes != null)
            {
                _logger.LogInformation("Returning notes from cache");
                return Ok(cachedNotes);
            }
            
            // Публикуем запрос
            var response = await PublishAndWaitResponseAsync("GET_ALL", new NoteRequestTo());
            
            if (response.Status == "SUCCESS" && response.Data != null)
            {
                var notes = JsonSerializer.Deserialize<List<NoteResponseTo>>(
                    response.Data.ToString()!);
                
                // Кэшируем результат
                await _cacheService.SetAsync(_notesListCacheKey, notes);
                
                return Ok(notes);
            }
            
            return Ok(new List<NoteResponseTo>());
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
    public async Task<ActionResult<NoteResponseTo>> GetNoteById(long id)
    {
        try
        {
            _logger.LogInformation("Getting note by id: {Id}", id);
            
            // Проверяем кэш
            var cacheKey = $"{_notesCachePrefix}{id}";
            var cachedNote = await _cacheService.GetAsync<NoteResponseTo>(cacheKey);
            
            if (cachedNote != null)
            {
                _logger.LogInformation("Returning note {Id} from cache", id);
                return Ok(cachedNote);
            }
            
            // Публикуем запрос
            var noteRequest = new NoteRequestTo { Id = id };
            var response = await PublishAndWaitResponseAsync("GET", noteRequest);
            
            if (response.Status == "SUCCESS" && response.Data != null)
            {
                var note = JsonSerializer.Deserialize<NoteResponseTo>(
                    response.Data.ToString()!);
                
                // Кэшируем результат
                await _cacheService.SetAsync(cacheKey, note);
                
                return Ok(note);
            }
            
            return NotFound($"Note with id {id} not found");
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error getting note by id: {Id}", id);
            return StatusCode(500, "An error occurred while retrieving the note");
        }
    }

    [HttpPut("{id:long}")]
    [ProducesResponseType(typeof(NoteResponseTo), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status500InternalServerError)]
    public async Task<ActionResult<NoteResponseTo>> UpdateNote(long id, [FromBody] NoteRequestTo note)
    {
        try
        {
            _logger.LogInformation("Updating note with id: {Id}", id);
            
            note.Id = id;
            var response = await PublishAndWaitResponseAsync("UPDATE", note);
            
            if (response.Status == "SUCCESS" && response.Data != null)
            {
                var updatedNote = JsonSerializer.Deserialize<NoteResponseTo>(
                    response.Data.ToString()!);
                
                // Обновляем кэш
                var cacheKey = $"{_notesCachePrefix}{id}";
                await _cacheService.SetAsync(cacheKey, updatedNote);
                
                // Инвалидируем кэш списка
                await _cacheService.RemoveAsync(_notesListCacheKey);
                
                return Ok(updatedNote);
            }
            
            return NotFound($"Note with id {id} not found");
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error updating note with id: {Id}", id);
            return StatusCode(500, "An error occurred while updating the note");
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
            _logger.LogInformation("Deleting note with id: {Id}", id);
            
            var noteRequest = new NoteRequestTo { Id = id };
            var response = await PublishAndWaitResponseAsync("DELETE", noteRequest);
            
            if (response.Status == "SUCCESS")
            {
                // Удаляем из кэша
                var cacheKey = $"{_notesCachePrefix}{id}";
                await _cacheService.RemoveAsync(cacheKey);
                
                // Инвалидируем кэш списка
                await _cacheService.RemoveAsync(_notesListCacheKey);
                
                return NoContent();
            }
            
            return NotFound($"Note with id {id} not found");
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error deleting note with id: {Id}", id);
            return StatusCode(500, "An error occurred while deleting the note");
        }
    }

    private async Task<KafkaResponse> PublishAndWaitResponseAsync(string operation, NoteRequestTo request)
    {
        var tcs = new TaskCompletionSource<KafkaResponse>();
        var correlationId = Guid.NewGuid().ToString();
        
        // Регистрируем обработчик ответа
        using var cts = new CancellationTokenSource(_requestTimeout);
        cts.Token.Register(() => tcs.TrySetCanceled());
        
        // Подписываемся на ответ
        var responseHandler = new EventHandler<KafkaResponse>((sender, response) =>
        {
            if (response.RequestId == request.Id && response.Operation == operation)
            {
                tcs.TrySetResult(response);
            }
        });
        
        // _producer.ResponseReceived += responseHandler;
        
        try
        {
            // Отправляем запрос
            await _producer.SendMessageAsync(request.Id?.ToString() ?? "0", new
            {
                Operation = operation,
                Data = request,
                CorrelationId = correlationId,
                Timestamp = DateTime.UtcNow
            });
            
            // Ждем ответ
            return await tcs.Task;
        }
        finally
        {
            // _producer.ResponseReceived -= responseHandler;
        }
    }
}

// класс для ответа
public class KafkaResponse
{
    public string Operation { get; set; } = string.Empty;
    public string Status { get; set; } = string.Empty;
    public object? Data { get; set; }
    public long RequestId { get; set; }
    public string? Error { get; set; }
}