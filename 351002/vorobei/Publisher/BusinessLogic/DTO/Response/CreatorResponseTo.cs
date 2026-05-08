using BusinessLogic.DTO.Request;
using DataAccess.Models;
using System.Text.Json.Serialization;

namespace BusinessLogic.DTO.Response
{
    public class CreatorResponseTo : BaseEntity
    {
        [JsonPropertyName("login")]
        public string Login { get; set; } = string.Empty;

        [JsonPropertyName("password")]
        public string Password { get; set; } = string.Empty;

        [JsonPropertyName("firstname")]
        public string FirstName { get; set; } = string.Empty;

        [JsonPropertyName("lastname")]
        public string LastName { get; set; } = string.Empty;
    }
}
