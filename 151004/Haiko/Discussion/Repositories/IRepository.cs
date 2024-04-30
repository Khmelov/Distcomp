namespace Discussion.Repositories
{
    public interface IRepository<T> where T : class
    {
        List<T> GetAll();
        T Get(int id);
        void Create(T item);
        void Update(T item);
        int Delete(int id);
    }
}
