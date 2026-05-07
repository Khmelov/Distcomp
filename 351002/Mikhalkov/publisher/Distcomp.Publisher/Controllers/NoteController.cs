using Distcomp.Application.DTOs;
using Distcomp.Application.Interfaces;
using Distcomp.Domain.Models;
using Distcomp.Infrastructure.Caching;
using Distcomp.Infrastructure.Messaging;
using Distcomp.Shared.Models;
using Microsoft.AspNetCore.Mvc;
using System.Security.Claims;
using System.Text.Json;

namespace Distcomp.WebApi.Controllers
{
    [ApiController]
    [Route("api/v1.0/notes")]
    [Route("api/v2.0/notes")]
    public class NoteController : ControllerBase
    {
        private readonly IRepository<Issue> _issueRepo;
        private readonly IUserService _userService;
        private readonly KafkaRequestReplyService _kafkaService;
        private readonly RedisCacheService _cache;

        public NoteController(IRepository<Issue> issueRepo, IUserService userService, KafkaRequestReplyService kafkaService, RedisCacheService cache)
        {
            _issueRepo = issueRepo;
            _userService = userService;
            _kafkaService = kafkaService;
            _cache = cache;
        }

        private bool IsV2 => Request.Path.Value?.Contains("/v2.0") ?? false;

        [HttpPost]
        public async Task<IActionResult> Create([FromBody] NoteRequestTo request)
        {
            if (IsV2 && !User.Identity!.IsAuthenticated)
                return Unauthorized(new { errorMessage = "Unauthorized", errorCode = 40101 });

            if (string.IsNullOrEmpty(request.Content) || request.Content.Length < 2 || request.Content.Length > 2048)
                return BadRequest(new { errorMessage = "Content length error", errorCode = 40008 });

            var issue = _issueRepo.GetById(request.IssueId);
            if (issue == null)
                return BadRequest(new { errorMessage = "Issue not found", errorCode = 40002 });

            if (IsV2 && User.FindFirst("role")?.Value == "CUSTOMER")
            {
                var currentUserLogin = User.FindFirst(ClaimTypes.NameIdentifier)?.Value
                                     ?? User.FindFirst("sub")?.Value;
                var owner = _userService.GetById(issue.UserId);
                if (owner?.Login != currentUserLogin)
                    return StatusCode(403, new { errorMessage = "Access denied. You don't own this issue.", errorCode = 40301 });
            }

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
            if (IsV2 && !User.Identity!.IsAuthenticated)
                return Unauthorized(new { errorMessage = "Unauthorized", errorCode = 40101 });

            var response = await _kafkaService.SendRequestAsync(new NoteOperationMessage { Operation = NoteOperation.GET_ALL });
            return ProcessKafkaResponse(response);
        }

        [HttpGet("{id:long}")]
        public async Task<IActionResult> GetById(long id)
        {
            if (IsV2 && !User.Identity!.IsAuthenticated)
                return Unauthorized(new { errorMessage = "Unauthorized", errorCode = 40101 });

            string cacheKey = $"note:{id}";
            var cachedNote = await _cache.GetAsync(cacheKey);
            if (!string.IsNullOrEmpty(cachedNote)) return Content(cachedNote, "application/json");

            var responseJson = await _kafkaService.SendRequestAsync(new NoteOperationMessage
            {
                Operation = NoteOperation.GET_BY_ID,
                NoteId = id
            });

            return ProcessKafkaResponse(responseJson, cacheKey: cacheKey);
        }

        [HttpPut("{id:long}")]
        public async Task<IActionResult> Update(long id, [FromBody] NoteRequestTo request)
        {
            if (IsV2)
            {
                var accessError = await CheckNoteAccess(id);
                if (accessError != null) return accessError;
            }

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
            if (IsV2)
            {
                var accessError = await CheckNoteAccess(id);
                if (accessError != null) return accessError;
            }

            await _cache.RemoveAsync($"note:{id}");

            var response = await _kafkaService.SendRequestAsync(new NoteOperationMessage
            {
                Operation = NoteOperation.DELETE,
                NoteId = id
            });
            return ProcessKafkaResponse(response, isDelete: true);
        }

        private async Task<IActionResult?> CheckNoteAccess(long noteId)
        {
            if (!User.Identity!.IsAuthenticated)
                return Unauthorized(new { errorMessage = "Unauthorized", errorCode = 40101 });

            if (User.FindFirst("role")?.Value == "ADMIN") return null;

            var responseJson = await _kafkaService.SendRequestAsync(new NoteOperationMessage { Operation = NoteOperation.GET_BY_ID, NoteId = noteId });
            if (responseJson == null) return StatusCode(504, "Timeout verifying access");

            using var doc = JsonDocument.Parse(responseJson);
            if (!doc.RootElement.TryGetProperty("data", out var data) || data.ValueKind == JsonValueKind.Null)
                return NotFound();

            var issueId = data.GetProperty("issueId").GetInt64();
            var issue = _issueRepo.GetById(issueId);
            var owner = _userService.GetById(issue?.UserId ?? 0);

            var currentUserLogin = User.FindFirst(ClaimTypes.NameIdentifier)?.Value ?? User.FindFirst("sub")?.Value;

            if (owner?.Login != currentUserLogin)
                return StatusCode(403, new { errorMessage = "Access denied. Not your note.", errorCode = 40301 });

            return null;
        }

        private IActionResult ProcessKafkaResponse(string? json, bool isDelete = false, string? cacheKey = null)
        {
            if (string.IsNullOrEmpty(json))
                return StatusCode(504, new { errorMessage = "Discussion timeout", errorCode = 50401 });

            try
            {
                using var doc = JsonDocument.Parse(json);
                if (!doc.RootElement.TryGetProperty("data", out var data) && !doc.RootElement.TryGetProperty("Data", out data))
                    return NotFound(new { errorMessage = "Note not found", errorCode = 40404 });

                if (data.ValueKind == JsonValueKind.Null)
                    return NotFound(new { errorMessage = "Note not found", errorCode = 40404 });

                if (isDelete) return NoContent();

                var rawJson = data.GetRawText();
                if (cacheKey != null) _cache.SetAsync(cacheKey, rawJson).Wait();

                return Content(rawJson, "application/json");
            }
            catch
            {
                return StatusCode(500, new { errorMessage = "Internal Error", errorCode = 50000 });
            }
        }
    }
}