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

        public override bool Equals(object obj)
        {
            if (obj is not CreatorResponseTo other)
                return false;

            return Id == other.Id &&
                   Login == other.Login &&
                   Password == other.Password &&
                   FirstName == other.FirstName &&
                   LastName == other.LastName;
        }

        public override int GetHashCode()
        {
            return HashCode.Combine(Id, Login, Password, FirstName, LastName);
        }
    }
}
