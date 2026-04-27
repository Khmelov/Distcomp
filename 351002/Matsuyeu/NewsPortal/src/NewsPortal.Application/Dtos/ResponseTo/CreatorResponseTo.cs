using System.Text.Json.Serialization;

namespace NewsPortal.Models.Dtos.RequestTo
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
