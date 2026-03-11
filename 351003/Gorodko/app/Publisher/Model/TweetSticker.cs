using System.Text.Json.Serialization;

namespace Publisher.Model {
    public class TweetSticker {
        public long TweetId { get; set; }
        [JsonIgnore]
        [System.ComponentModel.DataAnnotations.Schema.NotMapped]
        public Tweet? Tweet { get; set; }
        public long StickerId { get; set; }
        [JsonIgnore]
        [System.ComponentModel.DataAnnotations.Schema.NotMapped]
        public Sticker? Sticker { get; set; }
    }
}