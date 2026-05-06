using publisherApi.Models;

namespace RestApiTask.Models.DTOs
{
    public record WriterRequestTo(string Login, string Password, string Firstname, string Lastname, string? Role = null);
}
