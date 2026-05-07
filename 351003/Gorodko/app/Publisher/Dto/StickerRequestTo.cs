using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;

namespace Publisher.Dto {
    public class StickerRequestTo : BaseRequestTo {
        [Required]
        [StringLength(32, MinimumLength = 2)]
        [JsonPropertyName("name")]
        public string Name { get; set; } = string.Empty;
    }
}