using System.ComponentModel.DataAnnotations.Schema;

namespace DC_REST.Entities
{
	public class Issue_Label
	{
		public int Id { get; set; }
		public int IssueId { get; set; }
		public Issue? Issue { get; set; }

		public int LabelId { get; set; }
		public Label? Label { get; set; }
	}
}
