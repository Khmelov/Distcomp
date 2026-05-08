namespace RestApiTask.Models.Entities
{
    public class Article : IHasId
    {
        public long Id { get; set; }
        public long WriterId { get; set; }
        public string Title { get; set; } = string.Empty;
        public string Content { get; set; } = string.Empty;
        public DateTime Created { get; set; }
        public DateTime Modified { get; set; }

        // Для связи многие-ко-многим
        public List<long> MarkerIds { get; set; } = new();
    }
}
