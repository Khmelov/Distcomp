using Application.Interfaces;
using Core.Entities;
using System.Collections.Concurrent;

namespace Infrastructure.Persistence.InMemory
{
    public class InMemoryRepository<TEntity> : IRepository<TEntity>
        where TEntity : Entity
    {
        protected readonly ConcurrentDictionary<long, TEntity> _entities = new();

        private long _nextId = 1;
        
        private readonly Lock _lock = new();

        public virtual Task<TEntity?> FindAsync(ISpecification<TEntity> specification, CancellationToken cancellationToken = default)
        {
            cancellationToken.ThrowIfCancellationRequested();

            lock (_lock)
            {
                IQueryable<TEntity> query = ApplySpecification(specification);
                return Task.FromResult(query.FirstOrDefault());
            }
        }

        public virtual Task<IEnumerable<TEntity>> FindAllAsync(ISpecification<TEntity> specification, CancellationToken cancellationToken = default)
        {
            cancellationToken.ThrowIfCancellationRequested();

            lock (_lock)
            {
                IQueryable<TEntity> query = ApplySpecification(specification);
                return Task.FromResult(query.ToList().AsEnumerable());
            }
        }

        public virtual Task<int> CountAsync(ISpecification<TEntity> specification, CancellationToken cancellationToken = default)
        {
            cancellationToken.ThrowIfCancellationRequested();

            lock (_lock)
            {
                IQueryable<TEntity> query = ApplySpecification(specification);
                return Task.FromResult(query.Count());
            }
        }

        public virtual Task<bool> AnyAsync(ISpecification<TEntity> specification, CancellationToken cancellationToken = default)
        {
            cancellationToken.ThrowIfCancellationRequested();

            lock (_lock)
            {
                IQueryable<TEntity> query = ApplySpecification(specification);
                return Task.FromResult(query.Any());
            }
        }


        // CRUD 
        public virtual Task<TEntity> AddAsync(TEntity entity, CancellationToken cancellationToken = default)
        {
            ArgumentNullException.ThrowIfNull(entity);
            cancellationToken.ThrowIfCancellationRequested();

            lock (_lock)
            {
                var id = entity.Id;
                if (id == 0)
                {
                    id = _nextId;
                }

                if (_entities.TryAdd(_nextId, entity))
                {
                    entity.Id = id;
                    _nextId++;
                    return Task.FromResult(entity);
                }

                throw new InvalidOperationException($"Entity with id {id} already exists");
            }
        }

        public virtual Task<TEntity?> UpdateAsync(TEntity entity, CancellationToken cancellationToken = default)
        {
            ArgumentNullException.ThrowIfNull(entity);
            cancellationToken.ThrowIfCancellationRequested();

            lock (_lock)
            {
                var id = entity.Id;

                if (_entities.TryGetValue(id, out TEntity? updateEntity))
                {
                    _entities[id] = entity;
                    updateEntity = _entities[id];
                }

                return Task.FromResult(updateEntity);
            }
        }

        public virtual Task<TEntity?> DeleteAsync(TEntity entity, CancellationToken cancellationToken = default)
        {
            ArgumentNullException.ThrowIfNull(entity);
            cancellationToken.ThrowIfCancellationRequested();

            lock (_lock)
            {
                var id = entity.Id;
                _entities.TryRemove(id, out TEntity? deletedEntity);

                return Task.FromResult(deletedEntity);
            }
        }

        public virtual Task<bool> DeleteByIdAsync(long id, CancellationToken cancellationToken = default)
        {
            cancellationToken.ThrowIfCancellationRequested();

            lock (_lock)
            {
                return Task.FromResult(_entities.TryRemove(id, out _));
            }
        }

        public virtual Task<TEntity?> GetByIdAsync(long id, CancellationToken cancellationToken = default)
        {
            cancellationToken.ThrowIfCancellationRequested();

            lock (_lock)
            {
                return Task.FromResult(_entities.GetValueOrDefault(id));
            }
        }

        public virtual Task<IEnumerable<TEntity>> GetAllAsync(CancellationToken cancellationToken = default)
        {
            cancellationToken.ThrowIfCancellationRequested();

            lock (_lock)
            {
                return Task.FromResult(_entities.Values.AsEnumerable());
            }
        }

        protected virtual IQueryable<TEntity> ApplySpecification(ISpecification<TEntity> spec)
        {
            IQueryable<TEntity> query = _entities.Values.AsQueryable();

            if (spec.Criteria != null)
            {
                query = query.Where(spec.Criteria);
            }

            // TODO: инклюды

            if (spec.OrderBy != null)
            {
                query = query.OrderBy(spec.OrderBy);
            }
            else if (spec.OrderByDescending != null)
            {
                query = query.OrderByDescending(spec.OrderByDescending);
            }

            if (spec.IsPagingEnabled)
            {
                query = query.Skip(spec.Skip ?? 0)
                            .Take(spec.Take ?? 10);
            }

            return query;
        }

        public void Clear()
        {
            lock (_lock)
            {
                _entities.Clear();
            }
        }
    }
}
