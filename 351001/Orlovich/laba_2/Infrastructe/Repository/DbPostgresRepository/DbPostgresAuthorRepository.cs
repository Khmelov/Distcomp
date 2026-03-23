using Application.Repository;
using Domain.Models;
using Infrastructe.ApplicationDbContext; // Сохранил ваше название пространства имен
using Microsoft.EntityFrameworkCore;

namespace Infrastructe.Repository.DbPostgresRepository;

public class DbPostgresAuthorRepository : IAuthorRepository
{
    private readonly AppDbContext _сontext;

    public DbPostgresAuthorRepository(AppDbContext dbContext)
    {
        _сontext = dbContext;
    }

    public async Task AddAsync(Author editor)
    {
        await _сontext.Editor.AddAsync(editor);
        await _сontext.SaveChangesAsync();
    }

    public async Task<int> DeleteAsync(long id)
    {
        var entity = await _сontext.Editor.FindAsync(id);

        if (entity == null)
            return 0;

        _сontext.Editor.Remove(entity);
        return await _сontext.SaveChangesAsync();
    }

    public async Task<IList<Author>> GetAllAsync()
    {
        return await _сontext.Editor
            .AsNoTracking() 
            .ToListAsync();
    }

    public async Task<Author?> GetByIdAsync(long id)
    {
        return await _сontext.Editor
            .AsNoTracking()
            .FirstOrDefaultAsync(e => e.id == id);
    }

    public async Task<Author?> GetByLoginAsync(string login)
    {
        return await _сontext.Editor
            .AsNoTracking()
            .FirstOrDefaultAsync(e => e.login == login);
    }

    public async Task<Author?> UpdateAsync(long id, Author editor)
    {
        var existingEntity = await _сontext.Editor.FindAsync(id);

        if (existingEntity == null)
            return null;

        _сontext.Entry(existingEntity).CurrentValues.SetValues(editor);

        await _сontext.SaveChangesAsync();

        return existingEntity;
    }
}