using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Diagnostics.CodeAnalysis;
using System.Text.Json.Serialization;

namespace DistComp.Models {

    [Table("tbl_comment")]
    public class Comment : EntityWithId {

        [Column("topicId")]
        public long TopicId { get; set; }

        [Column("content")]
        [Length(2, 2048)]
        public string Content { get; set; } = null!;

        [JsonIgnore]
        [MaybeNull]
        public Topic Topic { get; set; } = null!;
    }

    public class CommentInDto {

        public long TopicId { get; init; }

        [Length(2, 2048)]
        public string Content { get; init; } = null!;
    }

    public class CommentOutDto {

        public long Id { get; init; }

        public long TopicId { get; init; }

        public string Content { get; init; } = null!;
    }
}
