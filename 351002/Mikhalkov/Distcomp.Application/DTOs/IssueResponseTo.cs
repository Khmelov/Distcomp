namespace Distcomp.Application.DTOs
{
    public record IssueResponseTo(
        long Id,
        long UserId,
        string Title,
        string Content,
        DateTime Created,
        DateTime Modified
    );
}
