using System.ComponentModel.DataAnnotations.Schema;

namespace DistComp.Models {

    [Table("tbl_topic_tag")]
    public class TopicTag {

        [Column("topicId")]
        public long TopicsId { get; set; }

        [Column("tagId")]
        public long TagsId { get; set; }

        public Topic Topic { get; set; } = null!;

        public Tag Tag { get; set; } = null!;
    }
}
