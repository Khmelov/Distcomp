namespace RW.Discussion.Caching;

public static class CacheKeys
{
    public static string Note(long id) => $"note:{id}";
    public const string NotesAll = "notes:all";
}
