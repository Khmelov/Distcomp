namespace NewsPortal.Models.Entities
{
    public class News
    {
        public long Id { get; set; }
        public long CreatorId { get; set; }
        public string Title { get; set; } = string.Empty;
        public string Content { get; set; } = string.Empty;
        public DateTime Created { get; set; }
        public DateTime Modified { get; set; }

        // Навигационные свойства
        public virtual Creator Creator { get; set; } = null!;
        public virtual ICollection<Mark> Marks { get; set; } = new List<Mark>();
        public virtual ICollection<Note> Notes { get; set; } = new List<Note>(); // Добавляем для связи с Note
    }
}