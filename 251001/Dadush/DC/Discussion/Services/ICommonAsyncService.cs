namespace Discussion.Services {
    public interface ICommonAsyncService<K, T, I, O> {

        Task<IEnumerable<O>> GetAllAsync();

        Task<O?> GetAsync(K id);

        Task<O> CreateAsync(I entity);

        Task<O> UpdateAsync(T entity);

        Task DeleteAsync(K id);
    }
}
