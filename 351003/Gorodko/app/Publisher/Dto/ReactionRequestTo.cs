using System.Text.Json.Serialization;

namespace Publisher.DTO {
    public enum ReactionState { PENDING, APPROVE, DECLINE }

    public class KafkaMessage {
        public string Operation { get; set; } 
        public ReactionRequestTo Data { get; set; }
        public string CorrelationId { get; set; }
    }

    public class ReactionRequestTo {
        public long Id { get; set; }
        public long TweetId { get; set; }
        public string Country { get; set; } = "by";
        public string Content { get; set; }
        public ReactionState State { get; set; } = ReactionState.PENDING;
    }
}