using System.Text.Json.Serialization;

namespace Publisher.Model {
    public class Sticker : BaseEntity {
        public string Name { get; set; } = string.Empty;
        [JsonIgnore]
        [System.ComponentModel.DataAnnotations.Schema.NotMapped]
        public List<Tweet> Tweets { get; set; } = new();
    }
}