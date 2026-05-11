using System.Net;
using System.Text;
using System.Text.Json;
using ServerApp.Models.DTOs.Requests;
using ServerApp.Models.DTOs.Responses;
using ServerApp.Models.Entities;
using ServerApp.Repository;
using ServerApp.Services.Interfaces;

namespace ServerApp.Services.Implementations;

public class MessageService(
    IRepository<Article> articleRepo, // Оставляем ArticleRepo для валидации
    HttpClient httpClient) : IMessageService
{
    private readonly JsonSerializerOptions _jsonOptions = new() { PropertyNameCaseInsensitive = true };

    // 1. Получить все сообщения (GET /api/v1.0/messages)
    public IEnumerable<MessageResponseTo> GetAll()
    {
        var response = httpClient.GetAsync("/api/v1.0/messages").Result;
        EnsureSuccess(response);

        var content = response.Content.ReadAsStringAsync().Result;
        return JsonSerializer.Deserialize<IEnumerable<MessageResponseTo>>(content, _jsonOptions)!;
    }

    // 2. Получить сообщение по ID (GET /api/v1.0/messages/{id})
    public MessageResponseTo GetById(long id)
    {
        var response = httpClient.GetAsync($"/api/v1.0/messages/{id}").Result;
        EnsureSuccess(response, id);

        var content = response.Content.ReadAsStringAsync().Result;
        return JsonSerializer.Deserialize<MessageResponseTo>(content, _jsonOptions)!;
    }

    // 3. Создать сообщение (POST /api/v1.0/messages)
    public MessageResponseTo Create(MessageRequestTo request)
    {
        // Бизнес-логика остается в Publisher: проверяем, существует ли статья в Postgres
        if (articleRepo.GetById(request.ArticleId) == null)
            throw new ArgumentException($"Article {request.ArticleId} not found");

        var response = httpClient.PostAsJsonAsync("/api/v1.0/messages", request).Result;
        EnsureSuccess(response);

        var content = response.Content.ReadAsStringAsync().Result;
        return JsonSerializer.Deserialize<MessageResponseTo>(content, _jsonOptions)!;
    }

    // 4. Обновить сообщение (PUT /api/v1.0/messages/{id})
    public MessageResponseTo Update(long id, MessageRequestTo request)
    {
        if (articleRepo.GetById(request.ArticleId) == null)
            throw new ArgumentException($"Article {request.ArticleId} not found");

        // Отправляем PUT запрос в DiscussionApp
        var jsonContent = new StringContent(JsonSerializer.Serialize(request), Encoding.UTF8, "application/json");
        var response = httpClient.PutAsync($"/api/v1.0/messages/{id}", jsonContent).Result;
        EnsureSuccess(response, id);

        var content = response.Content.ReadAsStringAsync().Result;
        return JsonSerializer.Deserialize<MessageResponseTo>(content, _jsonOptions)!;
    }

    // 5. Удалить сообщение (DELETE /api/v1.0/messages/{id})
    public void Delete(long id)
    {
        var response = httpClient.DeleteAsync($"/api/v1.0/messages/{id}").Result;
        EnsureSuccess(response, id);
    }

    // --- Вспомогательный метод для обработки ошибок от DiscussionApp ---
    private void EnsureSuccess(HttpResponseMessage response, long? id = null)
    {
        if (response.IsSuccessStatusCode) return;

        if (response.StatusCode == HttpStatusCode.NotFound)
            throw new KeyNotFoundException($"Message {id?.ToString() ?? "data"} not found in Discussion Service");

        if (response.StatusCode == HttpStatusCode.BadRequest)
            throw new ArgumentException("Invalid data sent to Discussion Service");

        throw new Exception($"Discussion Service error: {response.StatusCode}");
    }
}