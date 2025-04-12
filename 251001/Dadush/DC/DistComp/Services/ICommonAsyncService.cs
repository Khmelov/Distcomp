namespace DistComp.Services {
    public interface ICommonAsyncService<K, T, I, O> {

        Task<IEnumerable<O>> GetAll();

        Task<O?> Get(K id);

        Task<O> Create(I data);

        Task<O> Update(T data);

        Task Delete(K id);
    }
}
