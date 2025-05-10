namespace Discussion.Models;
using Cassandra.Mapping.Attributes;

public abstract class BaseModel
{
    [ClusteringKey]
    public long Id { get; set; }
}