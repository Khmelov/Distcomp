using System.ComponentModel.DataAnnotations.Schema;
using System.ComponentModel.DataAnnotations;

namespace DC_REST.Entities
{
	public class Note
	{
		public int Id { get; set; }

		public int IssueId { get; set; }

		public string Content { get; set; }

		public Issue Issue { get; set; }
	}
}
