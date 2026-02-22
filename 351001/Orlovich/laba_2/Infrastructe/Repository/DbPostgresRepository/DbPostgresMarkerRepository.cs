using Application.Repository;
using Domain.Models;
using Infrastructe.ApplicationDbContext;
using Microsoft.EntityFrameworkCore;

namespace Infrastructe.Repository.DbPostgresRepository;

public class DbPostgresMarkerRepository : IRepository<Marker>
{
    private readonly AppDbContext _context;

    public DbPostgresMarkerRepository(AppDbContext dbContext)
    {
        _context = dbContext;
    }

    public async Task AddAsync(Marker tag)
    {
        await _context.Tag.AddAsync(tag);
        await _context.SaveChangesAsync();
    }

    public async Task<int> DeleteAsync(long id)
    {
        var entity = await _context.Tag.FindAsync(id);

        if (entity == null)
            return 0;

        _context.Tag.Remove(entity);
        return await _context.SaveChangesAsync();
    }

    public async Task<IList<Marker>> GetAllAsync()
    {
        return await _context.Tag
            .AsNoTracking()
            .ToListAsync();
    }

    public async Task<Marker?> GetByIdAsync(long id)
    {
        return await _context.Tag
            .AsNoTracking()
            .FirstOrDefaultAsync(t => t.id == id);
    }

    public async Task<Marker?> UpdateAsync(long id, Marker tag)
    {
        var existingEntity = await _context.Tag.FindAsync(id);

        if (existingEntity == null)
            return null;

        _context.Entry(existingEntity).CurrentValues.SetValues(tag);
        await _context.SaveChangesAsync();

        return existingEntity;
    }
}