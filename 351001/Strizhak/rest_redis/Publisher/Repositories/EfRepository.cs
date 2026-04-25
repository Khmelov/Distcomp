using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Caching.Distributed;
using Publisher.Data;
using System.Linq.Expressions;
using System.Text.Json;

namespace Publisher.Repositories
{
    public class EfRepository<T> : IRepository<T> where T : class
    {
        protected readonly AppDbContext _context;
        protected readonly DbSet<T> _dbSet;
        protected readonly IDistributedCache _cache;
        private readonly string _entityName;

        public EfRepository(AppDbContext context, IDistributedCache cache)
        {
            _context = context;
            _dbSet = context.Set<T>();
            _cache = cache;
            _entityName = typeof(T).Name.ToLower();
        }

        private string GetKey(object id) => $"{_entityName}:{id}";
        private string GetListKey() => $"{_entityName}:all";

        public async Task<T?> GetByIdAsync(long id)
        {
            var key = GetKey(id);
            try
            {
                var cached = await _cache.GetStringAsync(key);
                if (!string.IsNullOrEmpty(cached))
                    return JsonSerializer.Deserialize<T>(cached);
            }
            catch { }

            var entity = await _dbSet.FindAsync(id);

            if (entity != null)
            {
                await SaveToCache(key, entity);
            }
            return entity;
        }

        public async Task<IEnumerable<T>> GetAllAsync()
        {
            var key = GetListKey();
            try
            {
                var cached = await _cache.GetStringAsync(key);
                if (!string.IsNullOrEmpty(cached))
                    return JsonSerializer.Deserialize<List<T>>(cached) ?? new List<T>();
            }
            catch { }

            var list = await _dbSet.ToListAsync();
            await SaveToCache(key, list);
            return list;
        }

        public async Task AddAsync(T entity)
        {
            await _dbSet.AddAsync(entity);
            InvalidateCache(null, GetListKey());
        }

        public void Update(T entity)
        {
            _dbSet.Update(entity);

            var id = entity.GetType().GetProperty("Id")?.GetValue(entity);
            InvalidateCache(id?.ToString(), GetListKey());
        }

        public void Delete(T entity)
        {
            _dbSet.Remove(entity);

            var id = entity.GetType().GetProperty("Id")?.GetValue(entity);
            InvalidateCache(id?.ToString(), GetListKey());
        }

        public async Task<IEnumerable<T>> FindAsync(Expression<Func<T, bool>> predicate)
            => await _dbSet.Where(predicate).ToListAsync();

        public async Task SaveChangesAsync() => await _context.SaveChangesAsync();

        public IQueryable<T> Query() => _dbSet.AsQueryable();

        private async Task SaveToCache(string key, object value)
        {
            try
            {
                var options = new DistributedCacheEntryOptions { AbsoluteExpirationRelativeToNow = TimeSpan.FromMinutes(5) };
                await _cache.SetStringAsync(key, JsonSerializer.Serialize(value), options);
            }
            catch { }
        }

        private void InvalidateCache(string? id, string listKey)
        {
            _cache.RemoveAsync(listKey);
            if (id != null)
            {
                _cache.RemoveAsync(GetKey(id));
            }
        }
    }
}