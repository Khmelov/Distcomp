namespace rest_api.Dtos
{
    public class UserResponseTo
    {
        public long Id { get; set; }
        public string Login { get; set; } = null!;
        public string Firstname { get; set; } = null!;
        public string Lastname { get; set; } = null!;
    }
}
