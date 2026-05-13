using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Redis.Data;
using Redis.Models;
using Redis.Services;

namespace Redis.Controllers;

[ApiController]
[Route("api/v1.0/news")] // Роут для новостей
public class NewsController : ControllerBase
{
    private readonly PublisherDbContext _context;
    private readonly ICacheService _cache;

    public NewsController(PublisherDbContext context, ICacheService cache)
    {
        _context = context;
        _cache = cache;
    }

    [HttpGet]
    public async Task<IActionResult> GetAll()
    {
        return Ok(await _context.News.ToListAsync());
    }

    [HttpGet("{id}")]
    public async Task<IActionResult> Get(int id)
    {
        var cacheKey = $"news_{id}";
        var cachedNews = await _cache.GetAsync<News>(cacheKey);
        if (cachedNews != null) return Ok(cachedNews);

        var news = await _context.News.FindAsync(id);
        if (news == null) return NotFound();

        await _cache.SetAsync(cacheKey, news, TimeSpan.FromMinutes(10));
        return Ok(news);
    }

    [HttpPost]
    public async Task<IActionResult> Create([FromBody] News news)
    {
        _context.News.Add(news);
        await _context.SaveChangesAsync();

        var cacheKey = $"news_{news.Id}";
        await _cache.SetAsync(cacheKey, news, TimeSpan.FromMinutes(10));

        return StatusCode(201, news);
    }

    [HttpPut]
    public async Task<IActionResult> Update([FromBody] News news)
    {
        _context.News.Update(news);
        await _context.SaveChangesAsync();
        
        await _cache.SetAsync($"news_{news.Id}", news, TimeSpan.FromMinutes(10));
        return Ok(news);
    }

    [HttpPut("{id}")]
    public async Task<IActionResult> UpdateById(int id, [FromBody] News news)
    {
        news.Id = id;
        _context.News.Update(news);
        await _context.SaveChangesAsync();
        
        await _cache.SetAsync($"news_{news.Id}", news, TimeSpan.FromMinutes(10));
        return Ok(news);
    }

    [HttpDelete("{id}")]
    public async Task<IActionResult> Delete(int id)
    {
        var news = await _context.News.FindAsync(id);
        if (news != null)
        {
            _context.News.Remove(news);
            await _context.SaveChangesAsync();
        }
        return NoContent();
    }
}