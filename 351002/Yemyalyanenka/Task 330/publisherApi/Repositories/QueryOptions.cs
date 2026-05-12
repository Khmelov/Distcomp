namespace RestApiTask.Repositories;

public sealed class QueryOptions
{
    public int PageNumber { get; init; } = 1;
    public int PageSize { get; init; } = 20;
    public string? SortBy { get; init; }
    public string SortOrder { get; init; } = "asc";
    public string? Filter { get; init; }
}

