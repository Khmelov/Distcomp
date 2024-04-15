using DC_REST.DTOs.Request;

namespace DC_REST.Validators
{
	public class IssueValidator : IValidator<IssueRequestTo>
	{
		public bool Validate(IssueRequestTo issueRequestTo)
		{
			if (issueRequestTo == null) return false;
			if (issueRequestTo.Title.Length < 2 || issueRequestTo.Title.Length > 64) return false;
			if (issueRequestTo.Content.Length < 4 || issueRequestTo.Content.Length > 2048) return false;
			return true;
		}
	}
}
