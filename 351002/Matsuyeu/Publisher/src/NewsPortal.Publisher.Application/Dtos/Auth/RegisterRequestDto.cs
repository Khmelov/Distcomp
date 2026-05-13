namespace Publisher.src.NewsPortal.Publisher.Application.Dtos.Auth
{
    public class RegisterRequestDto
    {
        public string Login { get; set; } = string.Empty;
        public string Password { get; set; } = string.Empty;
        public string FirstName { get; set; } = string.Empty;
        public string LastName { get; set; } = string.Empty;
        public string Role { get; set; } = "CUSTOMER";
    }
}
