// Data/Repositories/GenericRepository.cs
using System.Linq.Expressions;
using Microsoft.EntityFrameworkCore;
using Publisher.src.NewsPortal.Publisher.Domain.Entities;
using Publisher.src.NewsPortal.Publisher.Infrastructure.Data;
using Publisher.src.NewsPortal.Publisher.Infrastructure.Repositories.Abstractions;

namespace Publisher.src.NewsPortal.Publisher.Infrastructure.Repositories
{
    public class GenericRepository<T> : IRepository<T> where T : class
    {
        protected readonly PostgresDbContext _context;
        protected readonly DbSet<T> _dbSet;

        public GenericRepository(PostgresDbContext context)
        {
            _context = context;
            _dbSet = context.Set<T>();
        }

        public virtual async Task<IEnumerable<T>> GetAllAsync()
        {
            return await _dbSet.ToListAsync();
        }

        public virtual async Task<T?> GetByIdAsync(long id)
        {
            return await _dbSet.FindAsync(id);
        }

        public virtual async Task<T> AddAsync(T entity)
        {
            await _dbSet.AddAsync(entity);
            await _context.SaveChangesAsync();
            return entity;
        }

        public virtual async Task UpdateAsync(T entity)
        {
            // Проверяем, отслеживается ли уже сущность
            var trackedEntity = _context.ChangeTracker.Entries<T>()
                .FirstOrDefault(e => e.Entity == entity ||
                                    e.Property("Id") != null &&
                                     e.Property("Id").CurrentValue?.Equals(entity.GetType().GetProperty("Id")?.GetValue(entity)) == true);

            if (trackedEntity != null)
            {
                // Если отслеживается другая сущность с тем же ID, отсоединяем её
                trackedEntity.State = EntityState.Detached;
            }

            // Присоединяем и помечаем как измененную
            _dbSet.Update(entity);
            await _context.SaveChangesAsync();
        }

        public virtual async Task DeleteAsync(long id)
        {
            var entity = await GetByIdAsync(id);
            if (entity != null)
            {
                _dbSet.Remove(entity);
                await _context.SaveChangesAsync();
            }
        }

        public virtual async Task<bool> ExistsAsync(long id)
        {
            return await GetByIdAsync(id) != null;
        }

        public virtual async Task<PagedResult<T>> GetPagedAsync(QueryParameters parameters)
        {
            var query = _dbSet.AsQueryable();

            // Применяем поиск если есть
            if (!string.IsNullOrWhiteSpace(parameters.SearchTerm))
            {
                query = ApplySearch(query, parameters.SearchTerm);
            }

            // Применяем фильтрацию по дате для News
            if (typeof(T) == typeof(News) && parameters.FromDate.HasValue)
            {
                query = query.Where(e => EF.Property<DateTime>(e, "Created") >= parameters.FromDate.Value);
            }

            if (typeof(T) == typeof(News) && parameters.ToDate.HasValue)
            {
                query = query.Where(e => EF.Property<DateTime>(e, "Created") <= parameters.ToDate.Value);
            }

            // Подсчет общего количества
            var totalCount = await query.CountAsync();

            // Применяем сортировку
            if (!string.IsNullOrWhiteSpace(parameters.SortBy))
            {
                query = ApplySorting(query, parameters.SortBy, parameters.SortOrder);
            }

            // Применяем пагинацию
            var items = await query
                .Skip((parameters.PageNumber - 1) * parameters.PageSize)
                .Take(parameters.PageSize)
                .ToListAsync();

            return new PagedResult<T>
            {
                Items = items,
                TotalCount = totalCount,
                PageNumber = parameters.PageNumber,
                PageSize = parameters.PageSize
            };
        }

        public virtual async Task<IEnumerable<T>> FindAsync(Expression<Func<T, bool>> predicate)
        {
            return await _dbSet.Where(predicate).ToListAsync();
        }

        public virtual async Task<T?> FindSingleAsync(Expression<Func<T, bool>> predicate)
        {
            return await _dbSet.FirstOrDefaultAsync(predicate);
        }

        public virtual async Task<int> CountAsync(Expression<Func<T, bool>>? predicate = null)
        {
            if (predicate == null)
                return await _dbSet.CountAsync();

            return await _dbSet.CountAsync(predicate);
        }

        #region Private Methods

        private IQueryable<T> ApplySearch(IQueryable<T> query, string searchTerm)
        {
            // Поиск по текстовым полям в зависимости от типа сущности
            if (typeof(T) == typeof(Creator))
            {
                return query.Where(e =>
                    EF.Property<string>(e, "Login").Contains(searchTerm) ||
                    EF.Property<string>(e, "FirstName").Contains(searchTerm) ||
                    EF.Property<string>(e, "LastName").Contains(searchTerm));
            }

            if (typeof(T) == typeof(News))
            {
                return query.Where(e =>
                    EF.Property<string>(e, "Title").Contains(searchTerm) ||
                    EF.Property<string>(e, "Content").Contains(searchTerm));
            }

            if (typeof(T) == typeof(Note))
            {
                return query.Where(e =>
                    EF.Property<string>(e, "Content").Contains(searchTerm));
            }

            if (typeof(T) == typeof(Mark))
            {
                return query.Where(e =>
                    EF.Property<string>(e, "Name").Contains(searchTerm));
            }

            return query;
        }

        private IQueryable<T> ApplySorting(IQueryable<T> query, string sortBy, string? sortOrder)
        {
            var isDescending = sortOrder?.ToLower() == "desc";

            return sortBy.ToLower() switch
            {
                "id" => isDescending ? query.OrderByDescending(e => EF.Property<object>(e, "Id"))
                                     : query.OrderBy(e => EF.Property<object>(e, "Id")),

                "login" when typeof(T) == typeof(Creator) =>
                    isDescending ? query.OrderByDescending(e => EF.Property<string>(e, "Login"))
                                 : query.OrderBy(e => EF.Property<string>(e, "Login")),

                "name" when typeof(T) == typeof(Mark) =>
                    isDescending ? query.OrderByDescending(e => EF.Property<string>(e, "Name"))
                                 : query.OrderBy(e => EF.Property<string>(e, "Name")),

                "title" when typeof(T) == typeof(News) =>
                    isDescending ? query.OrderByDescending(e => EF.Property<string>(e, "Title"))
                                 : query.OrderBy(e => EF.Property<string>(e, "Title")),

                "created" when typeof(T) == typeof(News) =>
                    isDescending ? query.OrderByDescending(e => EF.Property<DateTime>(e, "Created"))
                                 : query.OrderBy(e => EF.Property<DateTime>(e, "Created")),

                _ => query.OrderBy(e => EF.Property<object>(e, "Id"))
            };
        }

        #endregion
    }
}