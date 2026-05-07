using System.ComponentModel.DataAnnotations;

namespace Distcomp.Application.DTOs
{
    public record IssueRequestTo(
        long? Id,
        long UserId,
        string Title,
        string Content,
        List<long>? MarkerIds,
        List<string>? Markers
    );
}
