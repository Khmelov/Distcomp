using RV_Kisel_lab2_Task320.Models.Dtos;
using System.Text.Json;
using System.Net.Http.Json;

namespace RV_Kisel_lab2_Task320.Services
{
    public interface IDiscussionServiceClient
    {
        Task<IEnumerable<PostDto>?> GetPostsByNewsId(int newsId);
        Task<PostDto?> CreatePost(CreatePostDto dto);
        Task DeletePost(int newsId, int postId); // Изменено на int
    }
    
    public class DiscussionServiceClient : IDiscussionServiceClient
    {
        private readonly HttpClient _httpClient;
        private readonly JsonSerializerOptions _jsonOptions;

        public DiscussionServiceClient(HttpClient httpClient)
        {
            _httpClient = httpClient;
            _jsonOptions = new JsonSerializerOptions { PropertyNameCaseInsensitive = true };
        }

        public async Task<IEnumerable<PostDto>?> GetPostsByNewsId(int newsId)
        {
            try
            {
                var response = await _httpClient.GetAsync($"/api/v1.0/news/{newsId}/posts");
                if (!response.IsSuccessStatusCode)
                {
                    return new List<PostDto>();
                }
                var content = await response.Content.ReadAsStringAsync();
                return JsonSerializer.Deserialize<IEnumerable<PostDto>>(content, _jsonOptions);
            }
            catch (HttpRequestException)
            {
                return new List<PostDto>();
            }
        }
        
        public async Task<PostDto?> CreatePost(CreatePostDto dto)
        {
            var response = await _httpClient.PostAsJsonAsync("/api/v1.0/posts", dto);
            response.EnsureSuccessStatusCode();
            var content = await response.Content.ReadAsStringAsync();
            return JsonSerializer.Deserialize<PostDto>(content, _jsonOptions);
        }
        
        public async Task DeletePost(int newsId, int postId) // Изменено на int
        {
            var response = await _httpClient.DeleteAsync($"/api/v1.0/posts/{postId}?newsId={newsId}");
            response.EnsureSuccessStatusCode();
        }
    }
}