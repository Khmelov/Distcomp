using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;

namespace Project.Dto {
    public class EditorRequestTo : BaseRequestTo {
        [Required]
        [StringLength(64, MinimumLength = 2)]
        [JsonPropertyName("login")]
        public string Login { get; set; } = string.Empty;

        [Required]
        [StringLength(128, MinimumLength = 8)]
        [JsonPropertyName("password")]
        public string Password { get; set; } = string.Empty;

        [Required]
        [StringLength(64, MinimumLength = 2)]
        [JsonPropertyName("firstname")]
        public string Firstname { get; set; } = string.Empty;

        [Required]
        [StringLength(64, MinimumLength = 2)]
        [JsonPropertyName("lastname")]
        public string Lastname { get; set; } = string.Empty;
    }
}