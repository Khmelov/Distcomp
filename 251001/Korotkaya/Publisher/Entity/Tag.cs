namespace WebApplication1.Entity
{
    public class Tag
    {
        public long Id { get; set; }
        public string Name { get; set; } = string.Empty;
        public List<Topic> Topics { get; set; } = new();
    }
}
