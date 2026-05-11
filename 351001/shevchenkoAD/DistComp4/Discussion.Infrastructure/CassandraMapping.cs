using Cassandra.Mapping;
using Discussion.Domain.Entities;

namespace Discussion.Infrastructure;

public class CassandraMapping : Mappings
{
    public CassandraMapping()
    {
        For<Comment>()
            .TableName("tbl_comment")
            .PartitionKey(c => c.IssueId)
            .ClusteringKey(c => c.Id)
            .Column(c => c.Id, cm => cm.WithName("id"))
            .Column(c => c.IssueId, cm => cm.WithName("issue_id"))
            .Column(c => c.Country, cm => cm.WithName("country"))
            .Column(c => c.Content, cm => cm.WithName("content"))
            .Column(c => c.State, cm => cm.WithName("state"));
    }
}