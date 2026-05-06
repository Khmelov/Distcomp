using Microsoft.EntityFrameworkCore;

public class CommonRepository<T> : IRepository<T> where T : class
{
    private readonly AppDbContext _context;
    private readonly DbSet<T> _dbSet;

    public CommonRepository(AppDbContext context)
    {
        _context = context;
        _dbSet = context.Set<T>();
    }

    public T? GetById(long id) => _dbSet.Find(id);

    public IEnumerable<T> GetAll() => _dbSet.ToList();

    public T Create(T entity)
    {
        _dbSet.Add(entity);
        _context.SaveChanges();
        return entity;
    }

    public T Update(T entity)
    {
        var idProperty = typeof(T).GetProperty("Id");
        if (idProperty == null)
            throw new InvalidOperationException($"Entity {typeof(T).Name} does not have Id property");

        var id = (long)idProperty.GetValue(entity)!;
        var existing = GetById(id);
        
        if (existing == null)
            throw new KeyNotFoundException($"{typeof(T).Name} with id={id} not found");

        _context.Entry(existing).CurrentValues.SetValues(entity);
        _context.SaveChanges();
        return existing;
    }

    public bool Delete(long id)
    {
        var entity = GetById(id);
        if (entity == null) return false;

        _dbSet.Remove(entity);
        _context.SaveChanges();
        return true;
    }
}