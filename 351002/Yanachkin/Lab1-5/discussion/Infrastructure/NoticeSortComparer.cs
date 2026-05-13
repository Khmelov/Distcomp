using discussion.Models.Domain;

namespace discussion.Infrastructure;

public static class NoticeSortComparer
{
    public static IComparer<NoticeEntity> For(string? sort)
    {
        return sort?.Trim().ToLowerInvariant() switch
        {
            "issueid,asc" => CompareBy((a, b) => a.IssueId.CompareTo(b.IssueId)),
            "issueid,desc" => CompareBy((a, b) => b.IssueId.CompareTo(a.IssueId)),
            "id,desc" => CompareBy((a, b) => b.Id.CompareTo(a.Id)),
            _ => CompareBy((a, b) => a.Id.CompareTo(b.Id))
        };
    }

    private static IComparer<NoticeEntity> CompareBy(Func<NoticeEntity, NoticeEntity, int> cmp) =>
        Comparer<NoticeEntity>.Create((a, b) => cmp(a, b));
}
