using System.Text.Json.Serialization;

namespace DataAccess.Models
{
    public class BaseEntity
    {
        [JsonPropertyName("id")]
        public int Id { get; set; }
    }
}