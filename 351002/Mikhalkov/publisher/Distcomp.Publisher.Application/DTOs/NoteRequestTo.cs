using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;

namespace Distcomp.Application.DTOs
{
    public record NoteRequestTo(
    [property: JsonPropertyName("id")] long? Id,
    [property: JsonPropertyName("issueId")] long IssueId,
    [property: JsonPropertyName("content")] string Content
    );
}
