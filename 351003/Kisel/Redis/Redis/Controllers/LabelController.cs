using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Redis.Data;
using Redis.Models;
using Redis.Services;

namespace Redis.Controllers;

[ApiController]
[Route("api/v1.0/labels")]
public class LabelController : ControllerBase
{
    private readonly PublisherDbContext _context;
    private readonly ICacheService _cache;

    public LabelController(PublisherDbContext context, ICacheService cache)
    {
        _context = context;
        _cache = cache;
    }

    [HttpGet]
    public async Task<IActionResult> GetAll()
    {
        return Ok(await _context.Labels.ToListAsync());
    }

    [HttpGet("{id}")]
    public async Task<IActionResult> Get(int id)
    {
        var cacheKey = $"label_{id}";
        var cachedLabel = await _cache.GetAsync<Label>(cacheKey);
        if (cachedLabel != null) return Ok(cachedLabel);

        var label = await _context.Labels.FindAsync(id);
        if (label == null) return NotFound();

        await _cache.SetAsync(cacheKey, label, TimeSpan.FromMinutes(10));
        return Ok(label);
    }

    [HttpPost]
    public async Task<IActionResult> Create([FromBody] Label label)
    {
        _context.Labels.Add(label);
        await _context.SaveChangesAsync();

        var cacheKey = $"label_{label.Id}";
        await _cache.SetAsync(cacheKey, label, TimeSpan.FromMinutes(10));

        return StatusCode(201, label);
    }

    [HttpPut]
    public async Task<IActionResult> Update([FromBody] Label label)
    {
        _context.Labels.Update(label);
        await _context.SaveChangesAsync();

        await _cache.SetAsync($"label_{label.Id}", label, TimeSpan.FromMinutes(10));
        return Ok(label);
    }

    [HttpPut("{id}")]
    public async Task<IActionResult> UpdateById(int id, [FromBody] Label label)
    {
        label.Id = id;
        _context.Labels.Update(label);
        await _context.SaveChangesAsync();

        await _cache.SetAsync($"label_{label.Id}", label, TimeSpan.FromMinutes(10));
        return Ok(label);
    }

    [HttpDelete("{id}")]
    public async Task<IActionResult> Delete(int id)
    {
        var label = await _context.Labels.FindAsync(id);

        // Если лейбл не найден, возвращаем 404 Not Found (как ждут тесты)
        if (label == null)
        {
            return NotFound();
        }

        _context.Labels.Remove(label);
        await _context.SaveChangesAsync();

        return NoContent();
    }
}