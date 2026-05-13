using lab1.Models.Entities;

namespace lab1.Common.Sorting;

public static class EditorSortResolvers
{
    public static Func<IQueryable<Editor>, IOrderedQueryable<Editor>>? Resolve(string? sort) =>
        sort?.Trim().ToLowerInvariant() switch
        {
            "login,asc" => q => q.OrderBy(e => e.Login),
            "login,desc" => q => q.OrderByDescending(e => e.Login),
            "id,asc" => q => q.OrderBy(e => e.Id),
            "id,desc" => q => q.OrderByDescending(e => e.Id),
            _ => null
        };
}

public static class IssueSortResolvers
{
    public static Func<IQueryable<Issue>, IOrderedQueryable<Issue>>? Resolve(string? sort) =>
        sort?.Trim().ToLowerInvariant() switch
        {
            "title,asc" => q => q.OrderBy(i => i.Title),
            "title,desc" => q => q.OrderByDescending(i => i.Title),
            "created,asc" => q => q.OrderBy(i => i.Created),
            "created,desc" => q => q.OrderByDescending(i => i.Created),
            "modified,asc" => q => q.OrderBy(i => i.Modified),
            "modified,desc" => q => q.OrderByDescending(i => i.Modified),
            "editorid,asc" => q => q.OrderBy(i => i.EditorId),
            "editorid,desc" => q => q.OrderByDescending(i => i.EditorId),
            "id,asc" => q => q.OrderBy(i => i.Id),
            "id,desc" => q => q.OrderByDescending(i => i.Id),
            _ => null
        };
}

public static class LabelSortResolvers
{
    public static Func<IQueryable<Label>, IOrderedQueryable<Label>>? Resolve(string? sort) =>
        sort?.Trim().ToLowerInvariant() switch
        {
            "name,asc" => q => q.OrderBy(l => l.Name),
            "name,desc" => q => q.OrderByDescending(l => l.Name),
            "id,asc" => q => q.OrderBy(l => l.Id),
            "id,desc" => q => q.OrderByDescending(l => l.Id),
            _ => null
        };
}

