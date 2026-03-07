namespace Additions.DAO;

public interface IBasicDAO<T> where T : Model<T>
{
    Task<T[]> GetAllAsync();
    Task<T> AddNewAsync(T model);
    Task DeleteAsync(long id);
    Task<T> GetByIdAsync(long id);
    Task<T> UpdateAsync(T model);
}