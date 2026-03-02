using rest_api.Entities;

namespace rest_api
{
    public class TopicTag
    {
        public long TopicId { get; set; }
        public long TagId { get; set; }

        public Topic Topic { get; set; }
        public Tag Tag { get; set; }
    }
}
