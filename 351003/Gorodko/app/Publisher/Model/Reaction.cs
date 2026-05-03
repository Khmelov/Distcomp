using System.Text.Json.Serialization;

namespace Publisher.Model {
    public class Reaction : BaseEntity {
        public long TweetId { get; set; }
        [JsonIgnore]
        [System.ComponentModel.DataAnnotations.Schema.NotMapped]
        public Tweet? Tweet { get; set; }
        public string Content { get; set; } = string.Empty;
    }
}