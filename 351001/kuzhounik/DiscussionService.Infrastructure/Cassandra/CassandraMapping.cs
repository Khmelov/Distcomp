using Cassandra.Mapping;
using DiscussionService.Domain.Entities;

namespace DiscussionService.Infrastructure.Cassandra;

public class CassandraMapping<Id> : Mappings
{
    public CassandraMapping()
    {
        For<Comment<Id>>() 
            .TableName("tbl_comment")
            .PartitionKey(c => c.StoryID)
            .ClusteringKey(c => c.ID)
            .Column(c => c.ID, cm => cm.WithName("id"))
            .Column(c => c.StoryID, cm => cm.WithName("story_id"))
            .Column(c => c.Content, cm => cm.WithName("content"));
    }
}