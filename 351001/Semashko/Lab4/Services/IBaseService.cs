namespace lab_1.Services
{
    public interface IBaseService<T, V>
    {
        public V? Create(T dto);

        V? Read(long id);

        public V? Update(T dto);

        public bool Delete(long id);

        public IEnumerable<V> GetAll();

        public Task<V> CreateAsync(T dto);
    }
}