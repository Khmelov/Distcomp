using System.Text.Json.Serialization;

namespace Publisher.Dto {
    public class EditorResponseTo : BaseResponseTo {
        [JsonPropertyName("login")]
        public string Login { get; set; } = string.Empty;

        [JsonPropertyName("firstname")]
        public string Firstname { get; set; } = string.Empty;

        [JsonPropertyName("lastname")]
        public string Lastname { get; set; } = string.Empty;
    }
}