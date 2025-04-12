using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;

namespace distcomp.DTOs
{
    public class ArticleRequestTo
    {
        [JsonPropertyName("id")]
        public long Id { get; set; }

        [JsonPropertyName("creatorId")]
        public long CreatorId { get; set; } //topic creator id

        [Required]
        [StringLength(64, MinimumLength = 2)]
        [JsonPropertyName("title")]
        public string Title { get; set; } 

        [Required]
        [StringLength(64, MinimumLength = 2)]
        [JsonPropertyName("content")]
        public string Content { get; set; } 
    }
}
