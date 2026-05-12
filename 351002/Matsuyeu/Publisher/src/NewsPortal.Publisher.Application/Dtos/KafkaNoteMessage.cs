using Publisher.src.NewsPortal.Publisher.Domain.Entities;

namespace Publisher.src.NewsPortal.Publisher.Application.Dtos
{
    public class KafkaNoteMessage
    {
        public string Action { get; set; } = string.Empty;
        public Note Data { get; set; } = new();
    }
}
