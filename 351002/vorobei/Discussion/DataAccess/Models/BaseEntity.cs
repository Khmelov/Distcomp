using System.Text.Json.Serialization;
using Cassandra.Mapping.Attributes;

namespace DataAccess.Models
{
    public class BaseEntity
    {
        [PartitionKey]
        [JsonPropertyName("id")]
        [Column("id", Type = typeof(int))]
        public int Id { get; set; }
    }
}
