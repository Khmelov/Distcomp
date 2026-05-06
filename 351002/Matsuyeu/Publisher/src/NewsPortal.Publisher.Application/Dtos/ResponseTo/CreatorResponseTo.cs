using System.Text.Json.Serialization;

namespace Publisher.src.NewsPortal.Publisher.Application.Dtos.ResponseTo
{
    // Response DTO - returned to client (no password)
    public class CreatorResponseTo
    {
        public long Id { get; set; }

        public string Login { get; set; }

        [JsonPropertyName("firstname")]
        public string FirstName { get; set; }

        [JsonPropertyName("lastname")]
        public string LastName { get; set; }
    }
}
