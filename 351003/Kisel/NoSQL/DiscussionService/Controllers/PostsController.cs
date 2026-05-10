using DiscussionService.Models.Dtos;
using DiscussionService.Services;
using Microsoft.AspNetCore.Mvc;

namespace DiscussionService.Controllers;

[ApiController]
[Route("api/v1.0/posts")]
public class PostController : ControllerBase
{
    private readonly IPostService _service;

    public PostController(IPostService service)
    {
        _service = service;
    }

    [HttpGet]
    public async Task<ActionResult> GetAll()
    {
        return Ok(await _service.GetAllAsync());
    }

    [HttpGet("{id}")]
    public async Task<ActionResult> GetById(int id)
    {
        var post = await _service.GetByIdAsync(id);
        if (post == null)
            return NotFound();

        return Ok(post);
    }

    // Эндпоинт для получения постов по ID новости (нужен для MainService)
    [HttpGet("/api/v1.0/news/{newsId}/posts")]
    public async Task<ActionResult> GetByNewsId(int newsId)
    {
        var allPosts = await _service.GetAllAsync();
        var filtered = allPosts.Where(p => p.NewsId == newsId).ToList();
        return Ok(filtered);
    }

    [HttpPost]
    public async Task<ActionResult> Create(CreatePostDto dto)
    {
        var post = await _service.CreateAsync(dto);
        return StatusCode(201, post);
    }

    [HttpPut]
    [HttpPut("{id}")]
    public async Task<ActionResult> Update(int? id, PostDto dto)
    {
        var targetId = id ?? dto.Id;
        await _service.UpdateAsync(targetId, dto);
        return Ok(dto);
    }

    [HttpDelete("{id}")]
    public async Task<ActionResult> Delete(int id)
    {
        await _service.DeleteAsync(id);
        return NoContent();
    }
}