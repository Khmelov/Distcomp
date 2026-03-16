using ArticleHouse.DAO.Models;

namespace ArticleHouse.DAO.Interfaces;

public interface IDAO<T> where T : Model<T>
{
    Task<T[]> GetAllAsync();
    Task<T> AddNewAsync(T model);
    Task DeleteAsync(long id);
    Task<T> GetByIdAsync(long id);
    Task<T> UpdateAsync(T model);
}