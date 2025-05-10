namespace Distributed_Computing_Lab_1.Repositories.Interfaces;

public interface IRepository<TEntity> where TEntity : class 
{
    Task<IEnumerable<TEntity>> GetAllAsync();

    Task<TEntity?> GetByIdAsync(long id);

    Task<TEntity> CreateAsync(TEntity entity);

    Task<TEntity?> UpdateAsync(TEntity entity);

    Task<TEntity?> DeleteAsync(long id);
}