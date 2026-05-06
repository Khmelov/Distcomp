using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Redis.Data;
using Redis.Models;
using Redis.Services;

namespace Redis.Controllers;

[ApiController]
[Route("api/v1.0/creators")] // Исправлено под тесты
public class CreatorController : ControllerBase
{
    private readonly PublisherDbContext _context;
    private readonly ICacheService _cache;

    public CreatorController(PublisherDbContext context, ICacheService cache)
    {
        _context = context;
        _cache = cache;
    }

    [HttpGet]
    public async Task<IActionResult> GetAll()
    {
        return Ok(await _context.Creators.ToListAsync());
    }

    [HttpGet("{id}")]
    public async Task<IActionResult> Get(int id)
    {
        var cacheKey = $"creator_{id}";
        var cachedCreator = await _cache.GetAsync<Creator>(cacheKey);
        if (cachedCreator != null) return Ok(cachedCreator);

        var creator = await _context.Creators.FindAsync(id);
        if (creator == null) return NotFound();

        await _cache.SetAsync(cacheKey, creator, TimeSpan.FromMinutes(10));
        return Ok(creator);
    }

    [HttpPost]
    public async Task<IActionResult> Create([FromBody] Creator creator)
    {
        _context.Creators.Add(creator);
        await _context.SaveChangesAsync();

        var cacheKey = $"creator_{creator.Id}";
        await _cache.SetAsync(cacheKey, creator, TimeSpan.FromMinutes(10));

        return StatusCode(201, creator); // Тесты ждут именно 201 Created
    }

    [HttpPut]
    public async Task<IActionResult> Update([FromBody] Creator creator)
    {
        _context.Creators.Update(creator);
        await _context.SaveChangesAsync();
        
        await _cache.SetAsync($"creator_{creator.Id}", creator, TimeSpan.FromMinutes(10));
        return Ok(creator);
    }

    [HttpPut("{id}")]
    public async Task<IActionResult> UpdateById(int id, [FromBody] Creator creator)
    {
        creator.Id = id;
        _context.Creators.Update(creator);
        await _context.SaveChangesAsync();
        
        await _cache.SetAsync($"creator_{creator.Id}", creator, TimeSpan.FromMinutes(10));
        return Ok(creator);
    }

    [HttpDelete("{id}")]
    public async Task<IActionResult> Delete(int id)
    {
        var creator = await _context.Creators.FindAsync(id);
        if (creator != null)
        {
            _context.Creators.Remove(creator);
            await _context.SaveChangesAsync();
        }
        return NoContent(); // Тесты ждут 204 NoContent
    }
}