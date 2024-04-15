namespace DC_REST.DTOs.Request
{
	public class IssueRequestTo
	{
		public int Id { get; set; }
		public int UserId { get; set; }
		public string Title { get; set; }
		public string Content { get; set; }
	}
}
