namespace Distcomp_NoteMicroservice.Repository;

public class PagedResult<T>
{
    public IEnumerable<T> Items { get; init; } = Enumerable.Empty<T>();
    
    public string? NextPagingState { get; init; }
    
    public bool HasMore => NextPagingState != null;

    public int Count => Items.Count();

    public static PagedResult<T> Empty => new() { Items = [] };
}