namespace RestApiTask.Repositories;

public sealed record PagedResult<T>(
    IReadOnlyList<T> Items,
    long TotalCount,
    int PageNumber,
    int PageSize
);

