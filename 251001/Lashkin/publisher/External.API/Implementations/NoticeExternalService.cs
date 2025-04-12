using System.Net;
using System.Text;
using System.Text.Json;
using Application.DTO.Request;
using Application.DTO.Response;
using Application.Exceptions;
using External.API.Response;
using External.Contracts.Interfaces;

namespace External.API.Implementations;

public class NoticeExternalService : INoticeExternalService<NoticeRequestTo, NoticeResponseTo>
{
    private readonly HttpClient _httpClient;
    
    public NoticeExternalService(HttpClient httpClient)
    {
        _httpClient = httpClient;
        _httpClient.BaseAddress = new Uri("http://discussion-api:24130/api/v1.0/");
    }
    
    public async Task<IEnumerable<NoticeResponseTo>> GetAsync(CancellationToken cancellationToken = default)
    {
        var responseMessage = await _httpClient.GetAsync("notices", cancellationToken);

        var noticesResponseTo = await DeserializeResponseAsync<IEnumerable<NoticeResponseTo>>(responseMessage, cancellationToken);

        return noticesResponseTo ?? new List<NoticeResponseTo>();
    }

    public async Task<NoticeResponseTo> GetByIdAsync(long id, CancellationToken cancellationToken = default)
    {
        var responseMessage = await _httpClient.GetAsync($"notices/{id}", cancellationToken);

        var noticeResponseTo = await DeserializeResponseAsync<NoticeResponseTo>(responseMessage, cancellationToken);

        return noticeResponseTo!;
    }

    public async Task<NoticeResponseTo> CreateAsync(NoticeRequestTo noticeRequestTo, CancellationToken cancellationToken = default)
    {
        var json = JsonSerializer.Serialize(noticeRequestTo);
        var content = new StringContent(json, Encoding.UTF8, "application/json");
        
        var responseMessage = await _httpClient.PostAsync("notices", content, cancellationToken);

        var noticeResponseTo = await DeserializeResponseAsync<NoticeResponseTo>(responseMessage, cancellationToken);

        return noticeResponseTo!;
    }

    public async Task<NoticeResponseTo> UpdateAsync(long id, NoticeRequestTo noticeRequestTo, CancellationToken cancellationToken = default)
    {
        var updateNoticeRequestTo = new UpdateNoticeRequestTo(id, noticeRequestTo.NewsId, noticeRequestTo.Content);
        var json = JsonSerializer.Serialize(updateNoticeRequestTo);
        var content = new StringContent(json, Encoding.UTF8, "application/json");
        
        var responseMessage = await _httpClient.PutAsync("notices", content, cancellationToken);

        var noticeResponseTo = await DeserializeResponseAsync<NoticeResponseTo>(responseMessage, cancellationToken);

        return noticeResponseTo!;
    }

    public async Task DeleteAsync(long id, CancellationToken cancellationToken = default)
    {
        var responseMessage = await _httpClient.DeleteAsync($"notices/{id}", cancellationToken);

        await DeserializeResponseAsync<NoticeResponseTo>(responseMessage, cancellationToken);
    }

    private async Task<T?> DeserializeResponseAsync<T>(HttpResponseMessage responseMessage, CancellationToken cancellationToken = default)
    {
        var rawResponse = await responseMessage.Content.ReadAsStringAsync(cancellationToken);
        
        if (!responseMessage.IsSuccessStatusCode)
        {
            var error = JsonSerializer.Deserialize<ErrorModel>(rawResponse);
            throw responseMessage.StatusCode switch
            {
                HttpStatusCode.NotFound => new NotFoundException(error!.ErrorMessage),
                HttpStatusCode.Conflict => new AlreadyExistsException(error!.ErrorMessage),
                _ => new Exception(error!.ErrorMessage)
            };
        }

        if (rawResponse == string.Empty)
        {
            return default;
        }
        
        var entities = JsonSerializer.Deserialize<T>(rawResponse, new JsonSerializerOptions
        {
            PropertyNameCaseInsensitive = true
        });

        return entities;
    }
}