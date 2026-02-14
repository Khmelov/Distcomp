using System.ComponentModel.DataAnnotations;

namespace Application.DTOs.Requests
{
    public class NewsRequestTo
    {
        public long? Id { get; set; }

        public long EditorId { get; set; }

        [StringLength(64, MinimumLength = 2)]
        public string Title { get; set; } = string.Empty;

        [StringLength(2048, MinimumLength = 4)]
        public string Content { get; set; } = string.Empty;

    }
}
