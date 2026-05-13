namespace lab1.Models.DTO.Requests
{
    public class EditorRequestTo
    {
        public long Id { get; set; }
        public string Login { get; set; } = null!;
        public string Password { get; set; } = null!;
        public string Firstname { get; set; } = null!;
        public string Lastname { get; set; } = null!;
        public string? Role { get; set; }
    }
}
