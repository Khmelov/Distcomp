using System.ComponentModel.DataAnnotations.Schema;
using System.ComponentModel.DataAnnotations;

namespace Discussion.Entities
{
	public class Note
	{
		public int Id { get; set; }

		public int issueid { get; set; }

		public string Content { get; set; }
	}
}
