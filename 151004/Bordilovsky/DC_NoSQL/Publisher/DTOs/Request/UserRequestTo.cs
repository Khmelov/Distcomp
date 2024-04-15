namespace DC_REST.DTOs.Request
{
	public class UserRequestTo
	{
		public int Id { get; set; }
		public string Login { get; set; }
		public string Password { get; set; }
		public string FirstName { get; set; }
		public string LastName { get; set; }
	}
}
