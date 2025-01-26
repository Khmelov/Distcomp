namespace Domain.Repositories;

public interface IRepositoryBase<T>
{
    Task<IEnumerable<T>> GetAll(bool trackChanges, CancellationToken cancellationToken = default);
    Task<T?> FindByIdAsync(long id, CancellationToken cancellationToken = default);
    Task CreateAsync(T entity, CancellationToken cancellationToken = default);
    void UpdateAsync(T entity);
    void DeleteAsync(T entity);
}