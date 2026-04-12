using Presentation.Contracts;

namespace Presentation.Clients;

public class ReactionsServiceClient 
{
    private readonly HttpClient _httpClient;

    public ReactionsServiceClient(HttpClient httpClient)
    {
        _httpClient = httpClient;
    }

    public async Task<List<ReactionResponse>?> GetAllReactions()
    {
        return await _httpClient.GetFromJsonAsync<List<ReactionResponse>>($"api/v1.0/reactions");
    }

    public async Task<ReactionResponse> CreateReaction(CreateReactionRequest request)
    {
        var response = await _httpClient.PostAsJsonAsync($"api/v1.0/reactions", request);
        response.EnsureSuccessStatusCode();
        var result = await response.Content.ReadFromJsonAsync<ReactionResponse>();
        
        return result ?? throw new Exception("Failed to create reaction");
    }

    public async Task<ReactionResponse?> GetReactionById(long id)
    {
        return await _httpClient.GetFromJsonAsync<ReactionResponse>($"api/v1.0/reactions/{id}");
    }

    public async Task<ReactionResponse?> UpdateReaction(ReactionUpdateRequest request)
    {
        var response = await _httpClient.PutAsJsonAsync($"api/v1.0/reactions", request);
        response.EnsureSuccessStatusCode();
        var result = await response.Content.ReadFromJsonAsync<ReactionResponse>();
        return result;
    }

    public async Task DeleteReaction(long id)
    {
        var response = await _httpClient.DeleteAsync($"api/v1.0/reactions/{id}");
        response.EnsureSuccessStatusCode();
    }
}
