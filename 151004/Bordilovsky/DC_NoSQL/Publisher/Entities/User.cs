namespace DC_REST.Entities
{
	public class User
	{

		public int Id { get; set; }

		public string Login { get; set; }

		public string Password { get; set; }

		public string FirstName { get; set; }

		public string LastName { get; set; }

		public List<Issue>? Issues { get; set; } = new();
	}
}
