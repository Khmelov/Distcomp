public interface IRepository<T>
{
    T? GetById(long id);      
    IEnumerable<T> GetAll();
    T Create(T entity);
    T Update(T entity);
    bool Delete(long id);
}