namespace Application.DTOs.Responses
{
    public class MarkerResponseTo(
        long id, 
        string name)
    {
        public long? Id { get; set; } = id;

        public string? Name { get; set; } = name;
    }
}
