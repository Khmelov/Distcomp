using Publisher.Proxies;
using Shared.Dtos;

namespace Publisher.Proxies
{
    public class ReactionProxy : IReactionProxy
    {
        private readonly HttpClient _httpClient;

        public ReactionProxy(IHttpClientFactory httpClientFactory)
        {
            _httpClient = httpClientFactory.CreateClient("DiscussionClient");
        }

        public async Task<ReactionResponseTo> GetByIdAsync(long topicId, long id)
        {
            var response = await _httpClient.GetAsync($"/api/v1.0/reactions/{topicId}/{id}");
            response.EnsureSuccessStatusCode();
            return await response.Content.ReadFromJsonAsync<ReactionResponseTo>();
        }

        public async Task<IEnumerable<ReactionResponseTo>> GetByTopicIdAsync(long topicId)
        {
            var response = await _httpClient.GetAsync($"/api/v1.0/reactions?topicId={topicId}");
            response.EnsureSuccessStatusCode();
            return await response.Content.ReadFromJsonAsync<IEnumerable<ReactionResponseTo>>();
        }

        public async Task<ReactionResponseTo> CreateAsync(ReactionRequestTo request)
        {
            var response = await _httpClient.PostAsJsonAsync("/api/v1.0/reactions", request);
            response.EnsureSuccessStatusCode();
            return await response.Content.ReadFromJsonAsync<ReactionResponseTo>();
        }

        public async Task<ReactionResponseTo> UpdateAsync(ReactionRequestTo request)
        {
            var response = await _httpClient.PutAsJsonAsync("/api/v1.0/reactions", request);
            response.EnsureSuccessStatusCode();
            return await response.Content.ReadFromJsonAsync<ReactionResponseTo>();
        }

        public async Task DeleteAsync(long topicId, long id)
        {
            var response = await _httpClient.DeleteAsync($"/api/v1.0/reactions/{topicId}/{id}");
            response.EnsureSuccessStatusCode();
        }
        public async Task<ReactionResponseTo> GetByIdOnlyAsync(long id)
        {
            var response = await _httpClient.GetAsync($"/api/v1.0/reactions/{id}");
            response.EnsureSuccessStatusCode();
            return await response.Content.ReadFromJsonAsync<ReactionResponseTo>();
        }
    }
}
