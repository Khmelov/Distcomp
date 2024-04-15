namespace Discussion.DTOs.Request
{
	public class NoteRequestTo
	{
		public int Id { get; set; }
		public int IssueId { get; set; }
		public string Content { get; set; }
	}
}
