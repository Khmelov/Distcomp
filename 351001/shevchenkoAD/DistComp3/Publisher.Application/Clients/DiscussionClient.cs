using System.Net.Http.Json;
using Microsoft.Extensions.Configuration;
using Publisher.Application.DTOs.Requests;
using Publisher.Application.DTOs.Responses;
using Publisher.Application.Exceptions;

namespace Publisher.Application.Clients;

public class DiscussionClient
{
    private readonly HttpClient _httpClient;

    public DiscussionClient(HttpClient httpClient, IConfiguration configuration)
    {
        _httpClient = httpClient;
        _httpClient.BaseAddress = new Uri(configuration["Services:DiscussionUrl"]!);
    }

    public async Task<CommentResponseTo> GetByIdAsync(long id)
    {
        var response = await _httpClient.GetAsync($"comments/{id}");
        if (response.StatusCode == System.Net.HttpStatusCode.NotFound) return null!;
        return (await response.Content.ReadFromJsonAsync<CommentResponseTo>())!;
    }

    public async Task<IEnumerable<CommentResponseTo>> GetByIssueIdAsync(long issueId)
    {
        return (await _httpClient.GetFromJsonAsync<IEnumerable<CommentResponseTo>>($"comments/issue/{issueId}"))!;
    }

    public async Task<CommentResponseTo> CreateAsync(CommentRequestTo request)
    {
        var response = await _httpClient.PostAsJsonAsync("comments", request);
     
        if (!response.IsSuccessStatusCode)
        {
            var error = await response.Content.ReadFromJsonAsync<ErrorResponse>();
            throw new RestException((int)response.StatusCode, error?.ErrorCode % 100 ?? 0, error?.ErrorMessage ?? "Remote error");
        }
        return (await response.Content.ReadFromJsonAsync<CommentResponseTo>())!;
    }

    public async Task<CommentResponseTo> UpdateAsync(CommentRequestTo request)
    {
        var response = await _httpClient.PutAsJsonAsync("comments", request);
        if (!response.IsSuccessStatusCode) throw new RestException(400, 40, "Error updating remote comment");
        return (await response.Content.ReadFromJsonAsync<CommentResponseTo>())!;
    }

    public async Task<bool> DeleteAsync(long id)
    {
        var response = await _httpClient.DeleteAsync($"comments/{id}");
        return response.IsSuccessStatusCode;
    }

    public async Task DeleteByIssueIdAsync(long issueId)
    {
        await _httpClient.DeleteAsync($"comments/issue/{issueId}");
    }
}