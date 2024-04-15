using System.ComponentModel.DataAnnotations;

namespace DC_REST.Entities
{
	public class Label
	{
		public int Id { get; set; }

		public string Name { get; set; }

		public List<Issue> Issues { get; set; } = new();
		public List<Issue_Label> Issue_Labels { get; set; } = new();
	}
}
