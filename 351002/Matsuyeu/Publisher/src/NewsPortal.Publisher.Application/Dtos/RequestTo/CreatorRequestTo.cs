using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;

namespace Publisher.src.NewsPortal.Publisher.Application.Dtos.RequestTo
{
    public class CreatorRequestTo
    {
        public long Id { get; set; }

        [Required]
        [StringLength(64, MinimumLength = 2)]
        [RegularExpression(@"^[a-zA-Z0-9_-]+$")]
        public string Login { get; set; }

        [Required]
        [StringLength(128, MinimumLength = 8)]
        public string Password { get; set; }

        [Required]
        [StringLength(64, MinimumLength = 2)]
        [RegularExpression(@"^[a-zA-Z0-9_-]+$")]
        [JsonPropertyName("firstname")]
        public string FirstName { get; set; }

        [Required]
        [StringLength(64, MinimumLength = 2)]
        [RegularExpression(@"^[a-zA-Z0-9_-]+$")]
        [JsonPropertyName("lastname")]
        public string LastName { get; set; }
    }
}
