using Distcomp.Application.DTOs;
using Distcomp.Application.Interfaces;
using Distcomp.Domain.Models;
using Distcomp.Infrastructure.Caching;
using Distcomp.Infrastructure.Messaging;
using Distcomp.Shared.Models;
using Microsoft.AspNetCore.Mvc;
using System.Text.Json;

[ApiController]
[Route("api/v1.0/notes")]
public class NoteController : ControllerBase
{
    private readonly IRepository<Issue> _issueRepo;
    private readonly KafkaRequestReplyService _kafkaService;
    private readonly RedisCacheService _cache;

    public NoteController(IRepository<Issue> issueRepo, KafkaRequestReplyService kafkaService, RedisCacheService cache)
    {
        _issueRepo = issueRepo;
        _kafkaService = kafkaService;
        _cache = cache;
    }

    [HttpPost]
    public async Task<IActionResult> Create([FromBody] NoteRequestTo request)
    {
        if (string.IsNullOrEmpty(request.Content) || request.Content.Length < 2 || request.Content.Length > 2048)
            return BadRequest(new { errorMessage = "Content length error", errorCode = 40008 });

        if (_issueRepo.GetById(request.IssueId) == null)
            return BadRequest(new { errorMessage = "Issue not found", errorCode = 40002 });

        var note = new Note
        {
            Id = request.Id ?? DateTime.UtcNow.Ticks,
            IssueId = request.IssueId,
            Content = request.Content,
            Country = "BY",
            State = NoteState.PENDING
        };

        var response = await _kafkaService.SendRequestAsync(new NoteOperationMessage
        {
            Operation = NoteOperation.CREATE,
            Note = note
        });

        if (response == null) return StatusCode(504, "Timeout");

        return CreatedAtAction(nameof(GetById), new { id = note.Id }, note);
    }

    [HttpGet]
    public async Task<IActionResult> GetAll()
    {
        var response = await _kafkaService.SendRequestAsync(new NoteOperationMessage { Operation = NoteOperation.GET_ALL });
        return ProcessKafkaResponse(response);
    }

    [HttpGet("{id:long}")]
    public async Task<IActionResult> GetById(long id)
    {
        string cacheKey = $"note:{id}";

        var cachedNote = await _cache.GetAsync(cacheKey);
        if (!string.IsNullOrEmpty(cachedNote))
        {
            return Content(cachedNote, "application/json");
        }

        var responseJson = await _kafkaService.SendRequestAsync(new NoteOperationMessage
        {
            Operation = NoteOperation.GET_BY_ID,
            NoteId = id
        });

        if (responseJson == null) return StatusCode(504, "Timeout");

        using var doc = JsonDocument.Parse(responseJson);

        if (!doc.RootElement.TryGetProperty("data", out var data) &&
            !doc.RootElement.TryGetProperty("Data", out data))
        {
            return NotFound();
        }

        if (data.ValueKind == JsonValueKind.Null) return NotFound();

        var rawNoteJson = data.GetRawText();
        await _cache.SetAsync(cacheKey, rawNoteJson); 

        return Content(rawNoteJson, "application/json");
    }

    [HttpPut("{id:long}")]
    public async Task<IActionResult> Update(long id, [FromBody] NoteRequestTo request)
    {
        await _cache.RemoveAsync($"note:{id}");

        var msg = new NoteOperationMessage
        {
            Operation = NoteOperation.UPDATE,
            NoteId = id,
            Note = new Note { Id = id, Content = request.Content, IssueId = request.IssueId, Country = "BY" }
        };
        var response = await _kafkaService.SendRequestAsync(msg);
        return ProcessKafkaResponse(response);
    }

    [HttpDelete("{id:long}")]
    public async Task<IActionResult> Delete(long id)
    {
        await _cache.RemoveAsync($"note:{id}");

        var response = await _kafkaService.SendRequestAsync(new NoteOperationMessage
        {
            Operation = NoteOperation.DELETE,
            NoteId = id
        });
        return ProcessKafkaResponse(response, isDelete: true);
    }

    private IActionResult ProcessKafkaResponse(string? json, bool isDelete = false)
    {
        if (string.IsNullOrEmpty(json))
            return StatusCode(504, new { errorMessage = "Discussion timeout", errorCode = 50401 });

        try
        {
            var options = new JsonSerializerOptions { PropertyNameCaseInsensitive = true };
            using var doc = JsonDocument.Parse(json);

            if (!doc.RootElement.TryGetProperty("data", out var data) &&
                !doc.RootElement.TryGetProperty("Data", out data))
            {
                return NotFound(new { errorMessage = "Note not found", errorCode = 40404 });
            }

            if (data.ValueKind == JsonValueKind.Null)
                return NotFound(new { errorMessage = "Note not found", errorCode = 40404 });

            if (isDelete) return NoContent();

            return Content(data.GetRawText(), "application/json");
        }
        catch
        {
            return StatusCode(500, new { errorMessage = "Internal Error", errorCode = 50000 });
        }
    }
}