using System.Net.Http.Json;

namespace Publisher.Services
{
    public interface IDiscussionClient
    {
        Task<IEnumerable<CommentResponseTo>> GetAllCommentsAsync();
        Task<CommentResponseTo> GetCommentByIdAsync(long id);
        Task<CommentResponseTo> CreateCommentAsync(CommentRequestTo request);
        Task<CommentResponseTo> UpdateCommentAsync(CommentRequestTo request);
        Task DeleteCommentAsync(long id);
    }

    public class DiscussionClient : IDiscussionClient
    {
        private readonly HttpClient _httpClient;

        public DiscussionClient(HttpClient httpClient)
        {
            _httpClient = httpClient;
            _httpClient.BaseAddress = new Uri("http://localhost:24130");
        }

        public async Task<IEnumerable<CommentResponseTo>> GetAllCommentsAsync()
        {
            return await _httpClient.GetFromJsonAsync<IEnumerable<CommentResponseTo>>("api/v1.0/comments") 
                   ?? new List<CommentResponseTo>();
        }

        public async Task<CommentResponseTo> GetCommentByIdAsync(long id)
        {
            return await _httpClient.GetFromJsonAsync<CommentResponseTo>($"api/v1.0/comments/{id}");
        }

        public async Task<CommentResponseTo> CreateCommentAsync(CommentRequestTo request)
        {
            var response = await _httpClient.PostAsJsonAsync("api/v1.0/comments", request);
            response.EnsureSuccessStatusCode();
            return await response.Content.ReadFromJsonAsync<CommentResponseTo>();
        }

        public async Task<CommentResponseTo> UpdateCommentAsync(CommentRequestTo request)
        {
            var response = await _httpClient.PutAsJsonAsync("api/v1.0/comments", request);
         
            response.EnsureSuccessStatusCode();
            return await response.Content.ReadFromJsonAsync<CommentResponseTo>();
        }

        public async Task DeleteCommentAsync(long id)
        {
            var response = await _httpClient.DeleteAsync($"api/v1.0/comments/{id}");
            response.EnsureSuccessStatusCode();
        }
    }

    public class CommentRequestTo
    {
        public long Id { get; set; }
        public long StoryId { get; set; }
        public string Content { get; set; } = "";
    }

    public class CommentResponseTo
    {
        public long Id { get; set; }
        public long StoryId { get; set; }
        public string Content { get; set; } = "";
    }
}