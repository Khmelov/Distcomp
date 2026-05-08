using System.Text.Json.Serialization;
using DataAccess.Models;

namespace BusinessLogic.DTO.Response
{
    public class PostResponseTo : BaseEntity
    {

        [JsonPropertyName("storyId")]
        public int StoryId { get; set; }

        [JsonPropertyName("content")]
        public string Content { get; set; } = string.Empty;

        public PostState State { get; set; } = PostState.PENDING;
    }
}
