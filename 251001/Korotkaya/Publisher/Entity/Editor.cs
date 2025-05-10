namespace WebApplication1.Entity
{
    public class Editor
    {
        public long Id { get; set; }
        public string Login { get; set; } = string.Empty;
        public string Password { get; set; } = string.Empty;
        public string Firstname { get; set; } = string.Empty;
        public string Lastname { get; set; } = string.Empty;
        public List<Topic> Topics { get; set; } = new();
    }
}
