namespace Application.DTOs.Responses
{
    public class EditorResponseTo(
        string login,
        string password,
        string firstName,
        string lastName)
    {
        public long Id { get; set; }

        public string Login { get; set; } = login;

        public string Password { get; set; } = password;

        public string Firstname { get; set; } = firstName;

        public string Lastname { get; set; } = lastName;

    }
}
