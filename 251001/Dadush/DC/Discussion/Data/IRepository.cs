namespace Discussion.Data {
    public interface IRepository<K, T> {

        IEnumerable<T> GetAll() {
            throw new NotSupportedException("Use async methods instead");
        }
        Task<IEnumerable<T>> GetAllAsync();

        T? Get(K id) {
            throw new NotSupportedException("Use async methods instead");
        }
        Task<T?> GetAsync(K id);

        T Create(T entity) {
            throw new NotSupportedException("Use async methods instead");
        }
        Task<T> CreateAsync(T entity);

        T Update(T entity) {
            throw new NotSupportedException("Use async methods instead");
        }
        Task<T> UpdateAsync(T entity);

        void Delete(K id) {
            throw new NotSupportedException("Use async methods instead");
        }
        Task DeleteAsync(K id);
    }
}
