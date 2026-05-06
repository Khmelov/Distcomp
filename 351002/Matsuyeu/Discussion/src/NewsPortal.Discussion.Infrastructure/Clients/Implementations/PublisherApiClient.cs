using Discussion.src.NewsPortal.Discussion.Infrastructure.Clients.Abstractions;

namespace Discussion.src.NewsPortal.Discussion.Infrastructure.Clients.Implementations
{
    public class PublisherApiClient : IPublisherApiClient
    {
        private readonly HttpClient _httpClient;
        private readonly ILogger<PublisherApiClient> _logger;

        public PublisherApiClient(HttpClient httpClient, ILogger<PublisherApiClient> logger)
        {
            _httpClient = httpClient;
            _logger = logger;
        }

        public async Task<bool> NewsExistsAsync(long newsId)
        {
            try
            {
                //Предполагаем, что в Publisher есть эндпоинт / api / v1.0 / news /{ id}
                var response = await _httpClient.GetAsync($"api/v1.0/news/{newsId}");
                _logger.LogWarning("Unexpected response from Publisher API: {StatusCode}, {newsId}", response.StatusCode, newsId);
                if (response.IsSuccessStatusCode)
                {

                    return true;
                }

                if (response.StatusCode == System.Net.HttpStatusCode.NotFound)
                {
                    return false;
                }

                _logger.LogWarning("Unexpected response from Publisher API: {StatusCode}", response.StatusCode);
                return true;
            }
            catch (HttpRequestException ex)
            {
                _logger.LogError(ex, "Failed to check news existence in Publisher API for newsId {NewsId}", newsId);
                throw new Exception($"Publisher API is unavailable: {ex.Message}");
            }
        }
    }
}
