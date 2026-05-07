using rest_api.Entities;
using System.ComponentModel.DataAnnotations;

namespace rest_api
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
