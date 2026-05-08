using Publisher.Entities;
using System.ComponentModel.DataAnnotations;

namespace Publisher.Entities
{
    public class TopicTag
    {
        [Required]
        public long TopicId { get; set; }
        [Required]
        public long TagId { get; set; }

        public Topic Topic { get; set; }
        public Tag Tag { get; set; }
    }
}
