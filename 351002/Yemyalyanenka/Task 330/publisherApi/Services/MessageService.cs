using System.Net.Http.Json;
using RestApiTask.Models.DTOs;
using RestApiTask.Services.Interfaces;
using RestApiTask.Repositories;

namespace RestApiTask.Services;

public class RemoteMessageService : IMessageService
{
    private readonly HttpClient _http;
    private const string BasePath = "api/v1.0/messages";

    public RemoteMessageService(HttpClient http) => _http = http;

    public async Task<IEnumerable<MessageResponseTo>> GetAllAsync(QueryOptions? options = null) =>
        await _http.GetFromJsonAsync<IEnumerable<MessageResponseTo>>(BasePath) ?? new List<MessageResponseTo>();

    public async Task<MessageResponseTo> GetByIdAsync(long id) =>
        await _http.GetFromJsonAsync<MessageResponseTo>($"{BasePath}/{id}")
        ?? throw new Exception("Not Found");

    public async Task<MessageResponseTo> CreateAsync(MessageRequestTo request)
    {
        var resp = await _http.PostAsJsonAsync(BasePath, request);
        return await resp.Content.ReadFromJsonAsync<MessageResponseTo>();
    }

    public async Task<MessageResponseTo> UpdateAsync(long id, MessageRequestTo request)
    {
        // Отправляем запрос на правильный URL с ID
        var resp = await _http.PutAsJsonAsync($"{BasePath}/{id}", request);
        resp.EnsureSuccessStatusCode();
        return await resp.Content.ReadFromJsonAsync<MessageResponseTo>();
    }

    public async Task DeleteAsync(long id) => await _http.DeleteAsync($"{BasePath}/{id}");
}