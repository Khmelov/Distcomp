using Core.Entities;

namespace Application.Interfaces
{
    public interface IRepository<TEntity> where TEntity : Entity
    {
        Task<TEntity?> GetByIdAsync(long id, CancellationToken cancellationToken = default);

        Task<TEntity?> FindAsync(ISpecification<TEntity> spec, CancellationToken ct = default);
        
        Task<IEnumerable<TEntity>> FindAllAsync(ISpecification<TEntity> spec, CancellationToken ct = default);

        Task<IEnumerable<TEntity>> GetAllAsync(CancellationToken ct = default);
        
        Task<int> CountAsync(ISpecification<TEntity> spec, CancellationToken ct = default);
        
        Task<bool> AnyAsync(ISpecification<TEntity> spec, CancellationToken ct = default);

        Task<TEntity> AddAsync(TEntity entity, CancellationToken ct = default);
        
        Task<TEntity?> UpdateAsync(TEntity entity, CancellationToken ct = default);
        
        Task<TEntity?> DeleteAsync(TEntity entity, CancellationToken ct = default);
    }
}
