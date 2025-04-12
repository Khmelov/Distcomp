using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Diagnostics.CodeAnalysis;
using System.Text.Json.Serialization;

namespace DistComp.Models {

    [Table("tbl_topic")]
    public class Topic : EntityWithId {

        [Column("user_id")]
        public long UserId { get; set; }

        [Column("title")]
        [Length(2, 64)]
        public string Title { get; set; } = null!;

        [Column("content")]
        [Length(4, 2048)]
        public string Content { get; set; } = null!;

        [Column("created")]
        public DateTime Created { get; set; }

        [Column("modified")]
        public DateTime Modified { get; set; }

        [NotMapped]
        [JsonPropertyName("tags")]
        public string[] TagStrings { get; set; } = [];

        [JsonIgnore]
        [MaybeNull]
        public User User { get; set; } = null!;

        [JsonIgnore]
        public ICollection<Comment> Comments { get; set; } = [];

        [JsonIgnore]
        public ICollection<Tag> Tags { get; set; } = [];
    }

    public class TopicInDto {
        public long UserId { get; init; }

        [Length(2, 64)]
        public string Title { get; init; } = null!;

        [Length(4, 2048)]
        public string Content { get; init; } = null!;

        public DateTime Created { get; init; }

        public DateTime Modified { get; init; }

        [JsonPropertyName("tags")]
        public string[] TagStrings { get; init; } = [];
    }

    public class TopicOutDto {
        public long Id { get; init; }

        public long UserId { get; init; }

        public string Title { get; init; } = null!;

        public string Content { get; init; } = null!;

        public DateTime Created { get; init; }

        public DateTime Modified { get; init; }
    }
}
