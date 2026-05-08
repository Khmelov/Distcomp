namespace NewsPortal.Models.Dtos.RequestTo
{
    public class NoteResponseTo
    {
        public long Id { get; set; }
        public long NewsId { get; set; }
        public string NewsTitle { get; set; } = string.Empty;
        public string Content { get; set; }
    }
}
