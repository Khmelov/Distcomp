namespace lab1.Models.DTO.Responses
{
    public class IssueResponseTo
    {
        public long Id { get; set; }
        public string Title { get; set; } = null!;
        public string Content { get; set; } = null!;
        public long EditorId { get; set; }
        public List<long> LabelIds { get; set; } = [];
    }
}
