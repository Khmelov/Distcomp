using Microsoft.EntityFrameworkCore;
using RV_Kisel_lab2_Task320.Data;
using RV_Kisel_lab2_Task320.Models.Dtos;
using RV_Kisel_lab2_Task320.Models.Entities;

namespace RV_Kisel_lab2_Task320.Services;

public class PostService : IPostService
{
    private readonly AppDbContext _context;
    public PostService(AppDbContext context) => _context = context;

    public async Task<IEnumerable<PostDto>> GetAllAsync() =>
        await _context.Posts.Select(p => new PostDto { Id = p.Id, Content = p.Content, NewsId = p.NewsId }).ToListAsync();

    public async Task<PostDto?> GetByIdAsync(int id)
    {
        var p = await _context.Posts.FindAsync(id);
        return p == null ? null : new PostDto { Id = p.Id, Content = p.Content, NewsId = p.NewsId };
    }

    public async Task<PostDto> CreateAsync(PostDto dto)
    {
        var post = new Post { Content = dto.Content, NewsId = dto.NewsId };
        _context.Posts.Add(post);
        await _context.SaveChangesAsync();
        dto.Id = post.Id;
        return dto;
    }

    public async Task UpdateAsync(int id, PostDto dto)
    {
        var p = await _context.Posts.FindAsync(id);
        if (p != null) { p.Content = dto.Content; p.NewsId = dto.NewsId; await _context.SaveChangesAsync(); }
    }

    public async Task DeleteAsync(int id)
    {
        var p = await _context.Posts.FindAsync(id);
        if (p != null) { _context.Posts.Remove(p); await _context.SaveChangesAsync(); }
    }
}