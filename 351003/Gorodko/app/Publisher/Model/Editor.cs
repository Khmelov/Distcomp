using System.Text.Json.Serialization;

namespace Publisher.Model {

    public enum UserRole { ADMIN, CUSTOMER }

    public class Editor : BaseEntity {
        public string Login { get; set; } = string.Empty;
        public string Password { get; set; } = string.Empty;
        public string Firstname { get; set; } = string.Empty;
        public string Lastname { get; set; } = string.Empty;
        public string Role { get; set; } = "CUSTOMER";

        [JsonIgnore]
        [System.ComponentModel.DataAnnotations.Schema.NotMapped]
        public List<Tweet> Tweets { get; set; } = new();
    }

}