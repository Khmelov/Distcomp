using System.ComponentModel.DataAnnotations;

namespace Application.DTOs.Requests
{
    public class PostRequestTo
    {
        public long? Id { get; set; }

        public long NewsId { get; set; }

        [StringLength(2048, MinimumLength = 4)]
        public string Content { get; set; }
    }
}
