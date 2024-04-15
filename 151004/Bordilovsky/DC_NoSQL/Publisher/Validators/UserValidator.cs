using DC_REST.DTOs.Request;

namespace DC_REST.Validators
{
	public class UserValidator : IValidator<UserRequestTo>
	{
		public bool Validate(UserRequestTo userRequestTo)
		{
			if (userRequestTo == null) return false; 
			if (userRequestTo.Login.Length < 2 || userRequestTo.Login.Length > 64) return false;
			if (userRequestTo.Password.Length < 8 || userRequestTo.Password.Length > 128) return false;
			if (userRequestTo.FirstName.Length < 2 || userRequestTo.FirstName.Length > 64) return false;
			if (userRequestTo.LastName.Length < 2 || userRequestTo.LastName.Length > 64) return false;
			return true;
		}
	}
}
