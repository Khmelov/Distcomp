using Application.Repository;
using Domain.Models;
using Infrastructe.ApplicationDbContext;
using Microsoft.EntityFrameworkCore;

namespace Infrastructe.Repository.DbPostgresRepository;

public class DbPostgresCommentRepository : IRepository<Comment>
{
    private readonly AppDbContext _context;

    public DbPostgresCommentRepository(AppDbContext dbContext)
    {
        _context = dbContext;
    }

    public async Task AddAsync(Comment story)
    {
        await _context.Story.AddAsync(story);
        await _context.SaveChangesAsync();
    }

    public async Task<int> DeleteAsync(long id)
    {
        var entity = await _context.Story.FindAsync(id);

        if (entity == null)
            return 0;

        _context.Story.Remove(entity);
        return await _context.SaveChangesAsync();
    }

    public async Task<IList<Comment>> GetAllAsync()
    {
        return await _context.Story
            .AsNoTracking()
            .ToListAsync();
    }

    public async Task<Comment?> GetByIdAsync(long id)
    {
        return await _context.Story
            .AsNoTracking()
            .FirstOrDefaultAsync(s => s.id == id);
    }

    public async Task<Comment?> UpdateAsync(long id, Comment story)
    {
        var existingEntity = await _context.Story.FindAsync(id);

        if (existingEntity == null)
            return null;

        _context.Entry(existingEntity).CurrentValues.SetValues(story);
        await _context.SaveChangesAsync();

        return existingEntity;
    }
}