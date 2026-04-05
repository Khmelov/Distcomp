namespace RestApiTask.Models.Entities
{
    public class Marker : IHasId
    {
        public long Id { get; set; }
        public string Name { get; set; } = string.Empty;

    }
}
