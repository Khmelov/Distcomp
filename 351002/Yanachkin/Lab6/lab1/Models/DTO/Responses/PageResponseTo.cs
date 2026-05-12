namespace lab1.Models.DTO.Responses;

public sealed class PageResponseTo<T>
{
    public List<T> Content { get; set; } = [];
    public int TotalElements { get; set; }
    public int TotalPages { get; set; }
    public int Number { get; set; }
    public int Size { get; set; }
}
