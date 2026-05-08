using System.Text.Json;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using RW.Api.Kafka;
using RW.Application.DTOs.Request;
using RW.Application.Exceptions;
using RW.Infrastructure.Data;

namespace RW.Api.Controllers.V2;

[ApiController]
[Route("api/v2.0/notes")]
[Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
public class NotesV2Controller : ControllerBase
{
    private readonly IKafkaRequestClient _kafka;
    private readonly ApplicationDbContext _db;

    private static readonly JsonSerializerOptions JsonOptions = new()
    {
        PropertyNamingPolicy = JsonNamingPolicy.CamelCase
    };

    public NotesV2Controller(IKafkaRequestClient kafka, ApplicationDbContext db)
    {
        _kafka = kafka;
        _db = db;
    }

    [HttpGet]
    public async Task<IActionResult> GetAll(CancellationToken ct)
    {
        var response = await _kafka.SendAndWaitAsync(KafkaMethods.GetAll, null, "all", ct);
        return BuildResult(response);
    }

    [HttpGet("{id:long}")]
    public async Task<IActionResult> GetById(long id, CancellationToken ct)
    {
        var response = await _kafka.SendAndWaitAsync(KafkaMethods.GetById, new { id }, id.ToString(), ct);
        return BuildResult(response);
    }

    [HttpPost]
    public async Task<IActionResult> Create([FromBody] NoteRequestTo dto, CancellationToken ct)
    {
        await EnsureCanWriteNoteAsync(dto.ArticleId);
        var response = await _kafka.SendAndWaitAsync(
            KafkaMethods.Create, dto, dto.ArticleId.ToString(), ct);
        return BuildResult(response, 201);
    }

    [HttpPut]
    public async Task<IActionResult> Update([FromBody] NoteRequestTo dto, CancellationToken ct)
    {
        await EnsureCanWriteNoteAsync(dto.ArticleId);
        var response = await _kafka.SendAndWaitAsync(
            KafkaMethods.Update, dto, dto.ArticleId.ToString(), ct);
        return BuildResult(response);
    }

    [HttpDelete("{id:long}")]
    public async Task<IActionResult> Delete(long id, CancellationToken ct)
    {
        // Customers can delete their own notes only via admin until note ownership is exposed; admins always allowed.
        if (!User.IsInRole("ADMIN"))
            throw new ForbiddenException("Only ADMIN can delete notes by id without context.");

        var response = await _kafka.SendAndWaitAsync(KafkaMethods.Delete, new { id }, id.ToString(), ct);
        if (response.Status == 204) return NoContent();
        return BuildResult(response);
    }

    private async Task EnsureCanWriteNoteAsync(long articleId)
    {
        if (User.IsInRole("ADMIN")) return;

        var article = await _db.Articles.AsNoTracking().FirstOrDefaultAsync(a => a.Id == articleId)
            ?? throw new NotFoundException("Article", articleId);

        // Customers may add notes to any article — but cannot moderate other authors' content.
        // The check below ensures an authenticated customer is the actor; ownership of notes
        // is determined by FirstName/LastName which we leave to the discussion service.
        var login = User.Identity?.Name;
        if (string.IsNullOrEmpty(login))
            throw new ForbiddenException("Authenticated user is required.");
    }

    private IActionResult BuildResult(KafkaResponseEnvelope envelope, int? successOverride = null)
    {
        var status = envelope.Status;
        if (status >= 200 && status < 300)
        {
            if (envelope.Data is null)
                return StatusCode(successOverride ?? status);
            var body = JsonSerializer.Deserialize<JsonElement>(JsonSerializer.Serialize(envelope.Data, JsonOptions));
            return StatusCode(successOverride ?? status, body);
        }

        var fiveDigitCode = status * 100;
        return StatusCode(status, new { errorCode = fiveDigitCode, errorMessage = envelope.Error ?? "Error" });
    }
}
