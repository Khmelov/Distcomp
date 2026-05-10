using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json;
using System.Text;

namespace MainService.Controllers;

[ApiController]
[Route("api/v1.0/posts")]
public class PostController : ControllerBase
{
    private readonly HttpClient _http;

    public PostController(IHttpClientFactory factory)
    {
        _http = factory.CreateClient();
    }

    const string discussionUrl = "http://localhost:24130/api/v1.0/posts";

    [HttpGet]
    public async Task<IActionResult> GetAll()
    {
        var r = await _http.GetAsync(discussionUrl);
        return StatusCode((int)r.StatusCode, await r.Content.ReadAsStringAsync());
    }

    [HttpGet("{id}")]
    public async Task<IActionResult> GetById(string id)
    {
        var r = await _http.GetAsync($"{discussionUrl}/{id}");
        return StatusCode((int)r.StatusCode, await r.Content.ReadAsStringAsync());
    }

    [HttpPost]
    public async Task<IActionResult> Create([FromBody] object dto)
    {
        var json = JsonConvert.SerializeObject(dto); 
        var r = await _http.PostAsync(
            discussionUrl,
            new StringContent(json, Encoding.UTF8, "application/json")
        );

        return StatusCode((int)r.StatusCode, await r.Content.ReadAsStringAsync());
    }

    [HttpPut]
    [HttpPut("{id}")] // Поддержка PUT с ID в URL
    public async Task<IActionResult> Update(string? id, [FromBody] object dto)
    {
        var json = JsonConvert.SerializeObject(dto);
        var url = string.IsNullOrEmpty(id) ? discussionUrl : $"{discussionUrl}/{id}";

        var r = await _http.PutAsync(
            url,
            new StringContent(json, Encoding.UTF8, "application/json")
        );

        return StatusCode((int)r.StatusCode, await r.Content.ReadAsStringAsync());
    }

    [HttpDelete("{id}")]
    public async Task<IActionResult> Delete(string id)
    {
        var r = await _http.DeleteAsync($"{discussionUrl}/{id}");
        return StatusCode((int)r.StatusCode, await r.Content.ReadAsStringAsync());
    }
}