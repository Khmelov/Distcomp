namespace Discussion.Validators
{
	public interface IValidator<TRequestTo>
	{
		bool Validate(TRequestTo requestTo);
	}
}
