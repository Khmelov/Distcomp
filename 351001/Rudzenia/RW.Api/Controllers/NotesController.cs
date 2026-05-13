using System.Text.Json;
using Microsoft.AspNetCore.Mvc;
using RW.Api.Kafka;
using RW.Application.DTOs.Request;

namespace RW.Api.Controllers;

[ApiController]
[Route("api/v1.0/notes")]
public class NotesController : ControllerBase
{
    private readonly IKafkaRequestClient _kafka;

    private static readonly JsonSerializerOptions JsonOptions = new()
    {
        PropertyNamingPolicy = JsonNamingPolicy.CamelCase
    };

    public NotesController(IKafkaRequestClient kafka)
    {
        _kafka = kafka;
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
        var response = await _kafka.SendAndWaitAsync(
            KafkaMethods.Create, dto, dto.ArticleId.ToString(), ct);
        return BuildResult(response, 201);
    }

    [HttpPut]
    public async Task<IActionResult> Update([FromBody] NoteRequestTo dto, CancellationToken ct)
    {
        var response = await _kafka.SendAndWaitAsync(
            KafkaMethods.Update, dto, dto.ArticleId.ToString(), ct);
        return BuildResult(response);
    }

    [HttpDelete("{id:long}")]
    public async Task<IActionResult> Delete(long id, CancellationToken ct)
    {
        var response = await _kafka.SendAndWaitAsync(KafkaMethods.Delete, new { id }, id.ToString(), ct);
        if (response.Status == 204) return NoContent();
        return BuildResult(response);
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

        return StatusCode(status, new { errorCode = status, errorMessage = envelope.Error ?? "Error" });
    }
}
