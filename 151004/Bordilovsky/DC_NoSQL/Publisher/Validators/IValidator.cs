namespace DC_REST.Validators
{
	public interface IValidator<TRequestTo>
	{
		bool Validate(TRequestTo requestTo);
	}
}
