using Application.Dtos;
using Application.Interfaces;
using Domain.Models;
using Microsoft.EntityFrameworkCore;

namespace Infrastructure.Repositories;

public class ReactionRepository : IReactionRepository
{
    private readonly BlogDbContext _context;

    public ReactionRepository(BlogDbContext context)
    {
        _context = context;
    }

    public async Task<List<Reaction>> GetAllReactionsAsync()
    {
        return await _context.Reactions.ToListAsync();
    }

    public async Task<Reaction?> GetReactionByIdAsync(long id)
    {
        return await _context
            .Reactions
            .AsNoTracking()
            .FirstOrDefaultAsync(r => r.Id == id);
    }

    public async Task<Reaction> CreateReactionAsync(ReactionCreateDto reactionCreateDto)
    {
        var reaction = new Reaction()
        {
            TopicId = reactionCreateDto.TopicId,
            Content = reactionCreateDto.Content
        };

        await _context.Reactions.AddAsync(reaction);
        await _context.SaveChangesAsync();

        return reaction;
    }

    public async Task DeleteReactionAsync(long id)
    {
        var reaction = (await _context.Reactions.FindAsync(id))!;

        _context.Reactions.Remove(reaction);
        await _context.SaveChangesAsync();
    }

    public async Task<Reaction> UpdateReactionAsync(ReactionUpdateDto reactionUpdateDto)
    {
        var reaction = (await _context.Reactions.FindAsync(reactionUpdateDto.Id))!;

        reaction.TopicId = reactionUpdateDto.TopicId;
        reaction.Content = reactionUpdateDto.Content;

        await _context.SaveChangesAsync();
        return reaction;
    }
}
