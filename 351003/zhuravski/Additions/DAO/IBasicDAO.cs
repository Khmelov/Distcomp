namespace Additions.DAO;

public interface IBasicDAO<T, X> where T : Model<T, X>
{
    Task<T[]> GetAllAsync();
    Task<T> AddNewAsync(T model);
    Task DeleteAsync(X id);
    Task<T> GetByIdAsync(X id);
    Task<T> UpdateAsync(T model);
}