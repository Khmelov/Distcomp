using Microsoft.EntityFrameworkCore;

public class PostgresRepository<TEntity> : IGenericRepository<TEntity> where TEntity : BaseEntity
{
    private readonly AppDbContext _context;
    private readonly DbSet<TEntity> _dbSet;

    public PostgresRepository(AppDbContext context)
    {
        _context = context;
        _dbSet = _context.Set<TEntity>();
    }

    public async Task<TEntity> GetByIdAsync(int id)
    {
        return await _dbSet.FindAsync(id);
    }

    public async Task<IEnumerable<TEntity>> GetAllAsync()
    {
        return await _dbSet.ToListAsync();
    }

    public async Task<TEntity> AddAsync(TEntity entity)
    {
        var addedEntity = await _dbSet.AddAsync(entity);
        await _context.SaveChangesAsync();
        return addedEntity.Entity;
    }

    public async Task<TEntity> UpdateAsync(TEntity entity)
    {
        _context.Entry(entity).State = EntityState.Modified;
        await _context.SaveChangesAsync();
        return entity;
    }

    public async Task<bool> DeleteAsync(int id)
    {
        var entity = await GetByIdAsync(id);
        if (entity == null)
            return false;

        _dbSet.Remove(entity);
        await _context.SaveChangesAsync();
        return true;
    }

    public async Task<bool> ExistsByLoginAsync(string login)
    {
        if (typeof(TEntity) == typeof(Editor))
        {
            return await _dbSet.OfType<Editor>().AnyAsync(e => e.login == login);
        }
        throw new InvalidOperationException("ExistsByLoginAsync is only supported for Editor entities.");
    }

    public async Task<bool> ExistsByTitleAsync(string title)
    {
        if (typeof(TEntity) == typeof(Story))
        {
            return await _dbSet.OfType<Story>().AnyAsync(e => e.Title == title);
        }
        throw new InvalidOperationException("ExistsByStoryAsync is only supported for Story entities.");
    }

    public async Task<bool> ExistsByIdAsync(int id)
    {
        if (typeof(TEntity) == typeof(Note))
        {
            return await _dbSet.AnyAsync(e => e.id == id);
        }
        throw new InvalidOperationException("ExistsByLoginAsync is only supported for Note entities.");
    }
}