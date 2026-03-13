using Application.Dtos;
using Application.Interfaces;
using Domain.Models;
using Microsoft.EntityFrameworkCore;

namespace Infrastructure.Repositories;

public class TopicRepository : ITopicRepository
{
    private readonly BlogDbContext _context;

    public TopicRepository(BlogDbContext context)
    {
        _context = context;
    }

    public async Task<List<Topic>> GetAllTopicsAsync()
    {
        return await _context.Topics.ToListAsync();
    }

    public async Task<Topic?> GetTopicByIdAsync(long id)
    {
        return await _context
            .Topics
            .AsNoTracking()
            .FirstOrDefaultAsync(t => t.Id == id);
    }

    public async Task<Topic?> GetTopicByTitle(string topic)
    {
        return await _context
            .Topics
            .AsNoTracking()
            .FirstOrDefaultAsync(x => x.Title == topic);
    }

    public async Task<Topic> CreateTopicAsync(TopicCreateDto topicCreateDto)
    {
        var topic = new Topic()
        {
            UserId = topicCreateDto.UserId,
            Title = topicCreateDto.Title,
            Content = topicCreateDto.Content,
        };
        
        await _context
            .Topics
            .AddAsync(topic);

        await _context.SaveChangesAsync();

        return topic;
    }

    public async Task DeleteTopicAsync(long id)
    {
        var topic = await _context
            .Topics
            .FindAsync(id);

        _context
            .Topics
            .Remove(topic!);

        await _context.SaveChangesAsync();
    }

    public async Task<Topic> UpdateTopicAsync(TopicUpdateDto topicUpdateDto)
    {
        var topic = await _context
            .Topics
            .FindAsync(topicUpdateDto.Id);
        
        topic!.Content = topicUpdateDto.Content;
        topic!.Title = topicUpdateDto.Title;
        topic!.UserId = topicUpdateDto.UserId;
        
        await _context.SaveChangesAsync();
        return topic;
    }
}
