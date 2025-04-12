public interface IGenericRepository<TEntity> where TEntity : BaseEntity
{
    Task<TEntity> GetByIdAsync(int id);
    Task<IEnumerable<TEntity>> GetAllAsync();
    Task<TEntity> AddAsync(TEntity entity);
    Task<TEntity> UpdateAsync(TEntity entity);
    Task<bool> DeleteAsync(int id);

    Task<bool> ExistsByLoginAsync(string login);
    Task<bool> ExistsByTitleAsync(string title);

    Task<bool> ExistsByIdAsync(int id);
}
