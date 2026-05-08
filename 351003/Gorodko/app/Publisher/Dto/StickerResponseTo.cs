using System.Text.Json.Serialization;

namespace Publisher.Dto {
    public class StickerResponseTo : BaseResponseTo {
        [JsonPropertyName("name")]
        public string Name { get; set; } = string.Empty;
    }
}