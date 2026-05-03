using System.Text.Json.Serialization;
using Cassandra.Mapping.Attributes;

namespace DataAccess.Models
{
    public class BaseEntity
    {
        [JsonPropertyName("id")]
        [Column("id")]
        public int Id { get; set; }
    }
}
