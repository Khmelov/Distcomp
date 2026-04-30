namespace NewsPortal.Models.Entities
{
    public class Creator
    {
        public long Id { get; set; }
        public string Login { get; set; }
        public string Password { get; set; }
        public string FirstName { get; set; }
        public string LastName { get; set; }
        public virtual ICollection<News> News { get; set; } = new List<News>();
    }
}