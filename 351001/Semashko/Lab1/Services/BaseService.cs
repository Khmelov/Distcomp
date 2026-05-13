
namespace lab_1.Services
{
    public interface BaseService<T, V>

    {
    public V Create(T dto);

    V? Read(long id);

    public V? Update(T dto);

    public bool Delete(long id);

    public List<V> GetAll();
    }
}
