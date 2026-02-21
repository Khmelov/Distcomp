namespace Distcomp.Application.Interfaces
{
    public interface IRepository<T>
    {
        T Create(T entity);
        T? GetById(long id);
        IEnumerable<T> GetAll();
        T Update(T entity);
        bool Delete(long id);
    }
}
