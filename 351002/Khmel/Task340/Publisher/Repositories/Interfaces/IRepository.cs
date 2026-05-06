public interface IRepository<T> where T : class
    {
        T? GetById(long id);
        IEnumerable<T> GetAll();
        T Create(T entity);
        T Update(T entity);
        bool Delete(long id);
    }