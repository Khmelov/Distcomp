using System.Text;
using System.Text.Json;
using Microsoft.AspNetCore.Mvc;
using RW.Application.DTOs.Request;
using RW.Application.DTOs.Response;

namespace RW.Api.Controllers;

[ApiController]
[Route("api/v1.0/notes")]
public class NotesController : ControllerBase
{
    private readonly HttpClient _httpClient;
    private static readonly JsonSerializerOptions JsonOptions = new()
    {
        PropertyNamingPolicy = JsonNamingPolicy.CamelCase
    };

    public NotesController(IHttpClientFactory httpClientFactory)
    {
        _httpClient = httpClientFactory.CreateClient("DiscussionService");
    }

    [HttpGet]
    public async Task<IActionResult> GetAll()
    {
        var response = await _httpClient.GetAsync("api/v1.0/notes");
        var content = await response.Content.ReadAsStringAsync();
        return StatusCode((int)response.StatusCode, JsonSerializer.Deserialize<JsonElement>(content));
    }

    [HttpGet("{id:long}")]
    public async Task<IActionResult> GetById(long id)
    {
        var response = await _httpClient.GetAsync($"api/v1.0/notes/{id}");
        var content = await response.Content.ReadAsStringAsync();
        return StatusCode((int)response.StatusCode, JsonSerializer.Deserialize<JsonElement>(content));
    }

    [HttpPost]
    public async Task<IActionResult> Create([FromBody] NoteRequestTo dto)
    {
        var json = JsonSerializer.Serialize(dto);
        var httpContent = new StringContent(json, Encoding.UTF8, "application/json");
        var response = await _httpClient.PostAsync("api/v1.0/notes", httpContent);
        var responseBody = await response.Content.ReadAsStringAsync();
        return StatusCode((int)response.StatusCode, JsonSerializer.Deserialize<JsonElement>(responseBody));
    }

    [HttpPut]
    public async Task<IActionResult> Update([FromBody] NoteRequestTo dto)
    {
        var json = JsonSerializer.Serialize(dto);
        var httpContent = new StringContent(json, Encoding.UTF8, "application/json");
        var response = await _httpClient.PutAsync("api/v1.0/notes", httpContent);
        var responseBody = await response.Content.ReadAsStringAsync();
        return StatusCode((int)response.StatusCode, JsonSerializer.Deserialize<JsonElement>(responseBody));
    }

    [HttpDelete("{id:long}")]
    public async Task<IActionResult> Delete(long id)
    {
        var response = await _httpClient.DeleteAsync($"api/v1.0/notes/{id}");
        if (response.StatusCode == System.Net.HttpStatusCode.NoContent)
            return NoContent();
        var content = await response.Content.ReadAsStringAsync();
        return StatusCode((int)response.StatusCode, JsonSerializer.Deserialize<JsonElement>(content));
    }
}
