using System.Text.Json.Serialization;

namespace Publisher.Dto {
    public abstract class BaseRequestTo {
        [JsonIgnore]
        public long Id { get; set; }
    }
}