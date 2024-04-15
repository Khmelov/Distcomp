namespace DC_REST.Entities
{
	public class Issue
	{
		public int Id { get; set; }
		public int UserId { get; set; }
		public string Title { get; set; }
		public string Content { get; set; }
		public DateTime Created { get; set; }
		public DateTime Modified { get; set; }

		public User User { get; set; }
		public List<Note>? Notes { get; set; } = new();
		public List<Label> Labels { get; set; } = new();
		public List<Issue_Label> Issue_Labels { get; set; } = new();
	}
}
