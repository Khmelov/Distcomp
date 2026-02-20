using System.ComponentModel.DataAnnotations;

namespace Distcomp.Application.DTOs
{
    public record NoteRequestTo(
        long? Id,
        long IssueId,
        string Content
    );
}
