using System.Linq.Expressions;
using BlogService.Infrastructure.PostgreSQL.Context;
using Microsoft.EntityFrameworkCore;
using Npgsql;
using Shared.Domain.Exceptions;
using Shared.Domain.Interfaces;

namespace BlogService.Infrastructure.PostgreSQL.Repositories;

public class BlogServiceEFRepository<Id, Entity> : IRepository<Id, Entity> where Entity : class, IEntity<Id>
{
    private readonly BlogServiceDbContext _context;
    private readonly DbSet<Entity> _dbSet;
    
    public BlogServiceEFRepository(BlogServiceDbContext context)
    {
        _context = context;
        _dbSet = _context.Set<Entity>();
    }
    
    public virtual async Task<Entity?> GetByIdAsync(Id id)
    {
        return await _dbSet.AsNoTracking().FirstOrDefaultAsync(e => e.ID.Equals(id));
    }

    public virtual async Task<IEnumerable<Entity>> GetAllAsync()
    {
        return await _dbSet.ToListAsync();
    }

    public virtual async Task AddAsync(Entity entity)
    {
        try
        {

            if (entity == null) throw new ArgumentNullException("entity не может быть null");
            _dbSet.Add(entity);
            await _context.SaveChangesAsync();
        }
        catch (DbUpdateException ex) when (ex.InnerException is PostgresException pgEx)
        {
            if (pgEx.SqlState == "23505") // Unique violation
                throw new AlreadyExistsException("Запись с таким значением уже существует");
            
            if (pgEx.SqlState == "23503") // FK violation
                throw new NotFoundException("Связанная сущность не найдена");

            throw;
        }
    }

    public virtual async Task UpdateAsync(Entity entity)
    {
        if (entity == null) throw new ArgumentNullException("entity не может быть null");
        _dbSet.Attach(entity);
        _context.Entry(entity).State = EntityState.Modified;
        await _context.SaveChangesAsync();
    }

    public virtual async Task DeleteAsync(Id id)
    {
        var entity = await _dbSet.FindAsync(id);
        if(entity == null) throw new InvalidOperationException("Unable to delete. Entity not found");
        if (_context.Entry(entity).State == EntityState.Detached)
        {
            _dbSet.Attach(entity);
        }
        _dbSet.Remove(entity);
        await _context.SaveChangesAsync();
    }

    public async Task<bool> ExistsAsync(Expression<Func<Entity, bool>> condition)
    {
        return await _context.Set<Entity>().AnyAsync(condition);
    }
}