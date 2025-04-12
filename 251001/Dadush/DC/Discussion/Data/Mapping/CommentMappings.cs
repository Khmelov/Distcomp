using Cassandra.Mapping;
using Discussion.Models;

namespace Discussion.Data.Mapping {
    public class CommentMappings : Mappings {

        public CommentMappings() {
            For<Comment>()
                .TableName("tbl_comment")
                .PartitionKey("country")
                .ClusteringKey("id")
                .Column(c => c.Country, config => config.WithName("country"))
                .Column(c => c.Id, config => config.WithName("id"))
                .Column(c => c.TopicId, config => config.WithName("topic_id"))
                .Column(c => c.Content, config => config.WithName("content"));
        }
    }
}
