namespace Publisher.src.NewsPortal.Publisher.Domain.Entities
{
    public class Mark
    {
        public long Id { get; set; }
        public string Name { get; set; }
        public virtual ICollection<News> News { get; set; } = new List<News>();
    }
}
