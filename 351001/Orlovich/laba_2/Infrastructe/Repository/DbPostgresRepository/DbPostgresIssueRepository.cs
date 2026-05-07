using Application.Repository;
using Domain.Models;
using Infrastructe.ApplicationDbContext;
using Microsoft.EntityFrameworkCore;

namespace Infrastructe.Repository.DbPostgresRepository;

public class DbPostgresIssueRepository : IIssueRepository
{
    private readonly AppDbContext _context;

    public DbPostgresIssueRepository(AppDbContext dbContext)
    {
        _context = dbContext;
    }

    public async Task AddAsync(Issue note)
    {
        await _context.Note.AddAsync(note);
        await _context.SaveChangesAsync();
    }

    public async Task<int> DeleteAsync(long id)
    {
        var entity = await _context.Note.FindAsync(id);

        if (entity == null)
            return 0;

        _context.Note.Remove(entity);
        return await _context.SaveChangesAsync();
    }

    public async Task<IList<Issue>> GetAllAsync()
    {
        return await _context.Note
            .AsNoTracking()
            .ToListAsync();
    }

    public async Task<Issue?> GetByIdAsync(long id)
    {
        return await _context.Note
            .AsNoTracking()
            .FirstOrDefaultAsync(n => n.id == id);
    }

    public async Task<Issue?> GetByTitleAsync(string title)
    {
        return await _context.Note
            .AsNoTracking()
            .FirstOrDefaultAsync(n => n.title == title);
    }

    public async Task<Issue?> UpdateAsync(long id, Issue note)
    {
        var existingEntity = await _context.Note.FindAsync(id);

        if (existingEntity == null)
            return null;

        _context.Entry(existingEntity).CurrentValues.SetValues(note);
        await _context.SaveChangesAsync();

        return existingEntity;
    }
}