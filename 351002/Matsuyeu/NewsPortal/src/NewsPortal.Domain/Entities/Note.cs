namespace NewsPortal.Models.Entities
{
    public class Note
    {
        public long Id { get; set; }
        public long NewsId { get; set; }
        public string Content { get; set; } = string.Empty;

        // Навигационное свойство
        public virtual News News { get; set; } = null!;
    }
}
