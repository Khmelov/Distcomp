namespace distcomp.Repositories
{
    public interface ICrudRepository<T, ID>
    {
        T Save(T entity); 
        T FindById(ID id);
        List<T> FindAll();
        T Update(T entity); 
        bool DeleteById(ID id); 
    }
}
