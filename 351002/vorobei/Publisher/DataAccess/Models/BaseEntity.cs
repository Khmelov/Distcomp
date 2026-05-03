using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;

namespace DataAccess.Models
{
    public class BaseEntity
    {
        [JsonPropertyName("id")]
        [Column("id")]
        public int Id { get; set; }
    }
}