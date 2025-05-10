using Cassandra.Mapping.Attributes;

namespace Discussion.Models;

public abstract class BaseModel
{
    [ClusteringKey]
    public long Id { get; set; }
}