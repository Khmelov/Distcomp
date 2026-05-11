namespace lab1.Infrastructure;

internal static class PublisherCacheKeys
{
    public static string NoticeById(long id) => $"distcomp:notice:{id}";
    public static string EditorById(long id) => $"distcomp:editor:{id}";
    public static string IssueById(long id) => $"distcomp:issue:{id}";
    public static string LabelById(long id) => $"distcomp:label:{id}";
}
