namespace Core.Entities
{
    public class Editor(
        string login,
        string password,
        string firstname,
        string lastname) : Entity
    {
        public string Login { get; set; } = login;

        public string Password { get; set; } = password;

        public string Firstname { get; set; } = firstname;

        public string Lastname { get; set; } = lastname;
    }
}
