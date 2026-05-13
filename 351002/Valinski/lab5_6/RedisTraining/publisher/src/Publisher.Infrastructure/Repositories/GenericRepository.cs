using Microsoft.EntityFrameworkCore;
using Publisher.Application.Repositories;
using Publisher.Infrastructure.DbContexts;

namespace Publisher.Infrastructure.Repositories;

public abstract class GenericRepository<T> : IGenericRepository<T> where T : class
{
    protected readonly PublisherDbContext Context;
    protected readonly DbSet<T> DbSet;

    protected GenericRepository(PublisherDbContext context)
    {
        Context = context;
        DbSet = Context.Set<T>();
    }

    public async Task<T?> GetByIdAsync(long id)
    {
        return await DbSet.FindAsync(id);
    }

    public async Task<List<T>> GetAllAsync()
    {
        return await DbSet.ToListAsync();
    }

    public async Task AddAsync(T entity)
    {
        await DbSet.AddAsync(entity);
        await Context.SaveChangesAsync();
    }

    public async Task<T> UpdateAsync(T entity)
    {
        DbSet.Update(entity);
        await Context.SaveChangesAsync();
        return entity;
    }

    public async Task DeleteAsync(T entity)
    {
        DbSet.Remove(entity);
        await Context.SaveChangesAsync();
    }
}
