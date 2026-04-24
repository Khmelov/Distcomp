using ServerApp.Models.Entities;

namespace ServerApp.Repository;

public interface IRepository<T> where T : BaseEntity
{
    IEnumerable<T> GetAll();
    T? GetById(long id);
    T Create(T entity);
    T Update(T entity);
    bool Delete(long id);
}