using Discussion.DTOs.Request;

namespace Discussion.Validators
{
	public class NoteValidator:IValidator<NoteRequestTo>
	{
		public bool Validate(NoteRequestTo noteRequestTo)
		{
			if (noteRequestTo == null) return false;
			if (noteRequestTo.Content.Length < 2 || noteRequestTo.Content.Length > 2048) return false;
			return true;
		}
	}
}
