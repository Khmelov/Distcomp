using System.Text.Json.Serialization;

namespace Publisher.Dto {
    public abstract class BaseResponseTo {
        [JsonPropertyName("id")]
        public long Id { get; set; }
    }
}