namespace lab1.Common.Paging;

public sealed class PagedResult<T>
{
    public IReadOnlyList<T> Content { get; init; } = [];
    public int TotalElements { get; init; }
    public int TotalPages { get; init; }
    public int Number { get; init; }
    public int Size { get; init; }

    public static PagedResult<T> Create(IReadOnlyList<T> items, int total, int page, int size)
    {
        var safeSize = size <= 0 ? 20 : size;
        var pages = total == 0 ? 0 : (int)Math.Ceiling(total / (double)safeSize);
        return new PagedResult<T>
        {
            Content = items,
            TotalElements = total,
            TotalPages = pages,
            Number = page,
            Size = safeSize
        };
    }
}
