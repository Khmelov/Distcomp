using System.ComponentModel.DataAnnotations;

namespace DiscussionService.DTOs.Requests
{
    public class MarkerRequestTo
    {
        public long? Id { get; set; }

        [StringLength(32, MinimumLength = 2)]
        public string? Name { get; set; }
    }
}
