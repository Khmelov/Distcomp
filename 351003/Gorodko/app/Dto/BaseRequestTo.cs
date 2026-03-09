using System.Text.Json.Serialization;

namespace Project.Dto {
    public abstract class BaseRequestTo {
        [JsonIgnore]
        public long Id { get; set; }
    }
}