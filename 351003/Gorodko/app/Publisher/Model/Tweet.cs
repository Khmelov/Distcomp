using Dapper;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;

namespace Publisher.Model {
    public class Tweet : BaseEntity {
        public long EditorId { get; set; }
        [JsonIgnore]
        [System.ComponentModel.DataAnnotations.Schema.NotMapped]
        public Editor? Editor { get; set; }
        public string Title { get; set; } = string.Empty;
        public string Content { get; set; } = string.Empty;
        public DateTime Created { get; set; }
        public DateTime Modified { get; set; }
        [JsonIgnore]
        [System.ComponentModel.DataAnnotations.Schema.NotMapped]

        public List<Reaction> Reactions { get; set; } = new();
        [JsonIgnore]
        [System.ComponentModel.DataAnnotations.Schema.NotMapped]
        public List<TweetSticker> TweetStickers { get; set; } = new();
    }
}