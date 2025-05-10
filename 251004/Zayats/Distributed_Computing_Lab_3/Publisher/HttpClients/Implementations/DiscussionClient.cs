using System.Text;
using System.Text.Json;
using Publisher.DTO.RequestDTO;
using Publisher.DTO.ResponseDTO;
using Publisher.HttpClients.Interfaces;

namespace Publisher.HttpClients.Implementations;

public class DiscussionClient : IDiscussionClient
{
    private static readonly JsonSerializerOptions? JsonSerializerOptions = new()
    {
        PropertyNameCaseInsensitive = true,
    };

    private readonly IHttpClientFactory _factory;

    public DiscussionClient(IHttpClientFactory factory)
    {
        _factory = factory;
    }
    
    public async Task<IEnumerable<MessageResponseDTO>?> GetNoticesAsync()
    {
        var httpClient = _factory.CreateClient(nameof(DiscussionClient));
        var uri = new Uri("messages", UriKind.Relative);
        var response = await httpClient.GetAsync(uri);
        response.EnsureSuccessStatusCode();

        var responseJson = await response.Content.ReadAsStringAsync();
        return JsonSerializer.Deserialize<IEnumerable<MessageResponseDTO>>(responseJson, JsonSerializerOptions);
    }

    public async Task<MessageResponseDTO?> GetNoticeByIdAsync(long id)
    {
        var httpClient = _factory.CreateClient(nameof(DiscussionClient));
        var response = await httpClient.GetAsync(new Uri($"messages/{id}", UriKind.Relative));
        response.EnsureSuccessStatusCode();

        var responseJson = await response.Content.ReadAsStringAsync();
        return JsonSerializer.Deserialize<MessageResponseDTO>(responseJson, JsonSerializerOptions);

    }

    public async Task<MessageResponseDTO?> CreateNoticeAsync(MessageRequestDTO post)
    {
        var httpClient = _factory.CreateClient(nameof(DiscussionClient));
        
        var postJson = JsonSerializer.Serialize(post);
        var content = new StringContent(postJson, Encoding.UTF8, "application/json");

        var response = await httpClient.PostAsync("messages", content);
        response.EnsureSuccessStatusCode();
        
        var responseJson = await response.Content.ReadAsStringAsync();
        return JsonSerializer.Deserialize<MessageResponseDTO>(responseJson, JsonSerializerOptions);
    }

    public async Task<MessageResponseDTO?> UpdateNoticeAsync(MessageRequestDTO post)
    {
        var httpClient = _factory.CreateClient(nameof(DiscussionClient));
        var postJson = JsonSerializer.Serialize(post);
        var content = new StringContent(postJson, Encoding.UTF8, "application/json");

        var response = await httpClient.PutAsync("messages", content);
        response.EnsureSuccessStatusCode();

        var responseJson = await response.Content.ReadAsStringAsync();
        return JsonSerializer.Deserialize<MessageResponseDTO>(responseJson, JsonSerializerOptions);
    }

    public async Task DeleteNoticeAsync(long id)
    {
        var httpClient = _factory.CreateClient(nameof(DiscussionClient));
        var response = await httpClient.DeleteAsync($"messages/{id}");
        response.EnsureSuccessStatusCode();
    }
}