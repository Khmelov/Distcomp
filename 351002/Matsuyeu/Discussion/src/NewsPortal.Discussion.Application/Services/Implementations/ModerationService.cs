using Discussion.src.NewsPortal.Discussion.Application.Services.Abstractions;

namespace Discussion.src.NewsPortal.Discussion.Application.Services.Implementations
{
    public class ModerationService : IModerationService
    {
        private readonly ILogger<ModerationService> _logger;
        private readonly HashSet<string> _stopWords = new(StringComparer.OrdinalIgnoreCase)
    {
        "spam", "bad", "hate", "abuse", "offensive", "inappropriate", "casino"
    };

        public ModerationService(ILogger<ModerationService> logger)
        {
            _logger = logger;
        }

        public string ModerateContent(string content)
        {
            if (string.IsNullOrWhiteSpace(content))
            {
                _logger.LogWarning("Empty content received for moderation");
                return "DECLINED";
            }

            var words = content.ToLower().Split(' ', StringSplitOptions.RemoveEmptyEntries);

            foreach (var word in words)
            {
                if (_stopWords.Contains(word))
                {
                    _logger.LogWarning("Stop word '{Word}' found in content", word);
                    return "DECLINED";
                }
            }

            if (content.Length < 2 || content.Length > 2048)
            {
                _logger.LogWarning("Content length {Length} is out of range", content.Length);
                return "DECLINED";
            }

            _logger.LogInformation("Content approved by moderation");
            return "APPROVED";
        }
    }
}
