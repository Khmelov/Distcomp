using BusinessLogic.Repositories;
using DataAccess.Models;
using Microsoft.EntityFrameworkCore;
using Infrastructure.DatabaseContext;

namespace Infrastructure.RepositoriesImplementation
{
    public class EfCoreRepository<T> : IRepository<T> where T : BaseEntity
    {
        protected readonly DistcompContext _context;
        protected readonly DbSet<T> _dbSet;

        public EfCoreRepository(DistcompContext context)
        {
            _context = context;
            _dbSet = context.Set<T>();
        }

        public async Task<List<T>> GetAllAsync()
        {
            return await _dbSet.ToListAsync();
        }

        public async Task<T?> GetByIdAsync(int id)
        {
            return await _dbSet.FindAsync(id);
        }

        public async Task<T> CreateAsync(T entity)
        {
            await _dbSet.AddAsync(entity);
            await _context.SaveChangesAsync();
            return entity;
        }

        public async Task<T> UpdateAsync(T entity)
        {
            _dbSet.Update(entity);
            await _context.SaveChangesAsync();
            return entity;
        }

        public async Task DeleteAsync(int id)
        {
            var entity = await GetByIdAsync(id);
            if (entity != null)
            {
                _dbSet.Remove(entity);
                await _context.SaveChangesAsync();
            }
        }

        public async Task<bool> ExistsAsync(int id)
        {
            return await _dbSet.AnyAsync(e => e.Id == id);
        }

        public async Task<int> GetLastIdAsync()
        {
            if (await _dbSet.AnyAsync())
                return await _dbSet.MaxAsync(e => e.Id);
            return 0;
        }
    }
}