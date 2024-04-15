using DC_REST.DTOs.Request;

namespace DC_REST.Validators
{
	public class LabelValidator:IValidator<LabelRequestTo>
	{
		public bool Validate(LabelRequestTo labelRequestTo)
		{
			if (labelRequestTo == null) return false;
			if (labelRequestTo.Name.Length < 2 || labelRequestTo.Name.Length > 32) return false;
			return true;
		}
	}
}
