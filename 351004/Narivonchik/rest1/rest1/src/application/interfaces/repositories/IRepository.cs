using rest1.core.entities;

namespace rest1.application.interfaces;

public interface IRepository<TEntity> where TEntity : Entity
{
    //get records
    Task<TEntity?> GetByIdAsync(long id, CancellationToken cancellationToken = default);
    Task<IEnumerable<TEntity>> GetAllAsync(CancellationToken ct = default);

    //create record
    Task<TEntity> AddAsync(TEntity entity, CancellationToken ct = default);
        
    //update record
    Task<TEntity?> UpdateAsync(TEntity entity, CancellationToken ct = default);
    
    //delete record
    Task<TEntity?> DeleteAsync(TEntity entity, CancellationToken ct = default);
    
    //find records
    Task<TEntity?> FindAsync(ISpecification<TEntity> spec, CancellationToken ct = default);
    Task<IEnumerable<TEntity>> FindAllAsync(ISpecification<TEntity> spec, CancellationToken ct = default);
    
    //count records
    Task<int> CountAsync(ISpecification<TEntity> spec, CancellationToken ct = default);
    
    //is any record
    Task<bool> AnyAsync(ISpecification<TEntity> spec, CancellationToken ct = default);
}