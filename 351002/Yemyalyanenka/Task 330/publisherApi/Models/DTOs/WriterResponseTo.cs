namespace RestApiTask.Models.DTOs
{
    public class WriterResponseTo
    {
        public long Id { get; set; }
        public string Login { get; set; } = string.Empty;
        public string Password { get; set; } = string.Empty;
        public string Firstname { get; set; } = string.Empty;
        public string Lastname { get; set; } = string.Empty;
    }

}
