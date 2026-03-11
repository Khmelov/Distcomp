using System.Text.Json.Serialization;

namespace Project.Dto {
    public abstract class BaseResponseTo {
        [JsonPropertyName("id")]
        public long Id { get; set; }
    }
}