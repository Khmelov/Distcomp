using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Http;
using System.Net.Http.Json;
using System.Threading.Tasks;
using WebApplication1.DTO;

namespace WebApplication1.Service
{
    public class RemoteReactionService : IRemoteReactionService
    {
        private readonly KafkaReactionProducerService _producerService;
        private readonly HttpClient _httpClient;
        private readonly IRedisCacheService _redisCacheService;

        public RemoteReactionService(
            KafkaReactionProducerService producerService,
            HttpClient httpClient,
            IRedisCacheService redisCacheService)
        {
            _producerService = producerService;
            _httpClient = httpClient;
            _redisCacheService = redisCacheService;
        }
        public async Task<ReactionResponseTo> CreateReactionAsync(ReactionRequestTo dto)
        {
            if (!dto.Id.HasValue)
                dto.Id = DateTimeOffset.UtcNow.ToUnixTimeMilliseconds();

            var response = await _httpClient.PostAsJsonAsync("reactions", dto);
            response.EnsureSuccessStatusCode();

            var created = await response.Content.ReadFromJsonAsync<ReactionResponseTo>();

            var cacheKey = $"reaction_{created.Id}";
            await _redisCacheService.SetAsync(cacheKey, created);

            await _producerService.SendReactionAsync(dto);

            return created;
        }
        public async Task<ReactionResponseTo> UpdateReactionAsync(ReactionRequestTo dto)
        {
            var response = await _httpClient.PutAsJsonAsync($"reactions/{dto.Id}", dto);
            response.EnsureSuccessStatusCode();

            var updated = await response.Content.ReadFromJsonAsync<ReactionResponseTo>();

            var cacheKey = $"reaction_{updated.Id}";
            await _redisCacheService.SetAsync(cacheKey, updated);

            await _producerService.SendReactionAsync(dto);

            return updated;
        }

        public async Task DeleteReactionAsync(string id)
        {
            var response = await _httpClient.DeleteAsync($"reactions/{id}");
            response.EnsureSuccessStatusCode();

            var cacheKey = $"reaction_{id}";
            await _redisCacheService.RemoveAsync(cacheKey);
        }

        public async Task<List<ReactionResponseTo>> GetAllReactionsAsync()
        {
            return await _httpClient.GetFromJsonAsync<List<ReactionResponseTo>>("reactions");
        }

        public async Task<ReactionResponseTo> GetReactionByIdAsync(long id)
        {
            var cacheKey = $"reaction_{id}";

            var cached = await _redisCacheService.GetAsync<ReactionResponseTo>(cacheKey);
            if (cached != null)
                return cached; 

            var response = await _httpClient.GetAsync($"reactions/{id}");
            if (response.StatusCode == HttpStatusCode.NotFound)
                return null;

            response.EnsureSuccessStatusCode();

            var reaction = await response.Content.ReadFromJsonAsync<ReactionResponseTo>();
            if (reaction != null)
            {
                await _redisCacheService.SetAsync(cacheKey, reaction);
            }
            return reaction;
        }

    }
}
