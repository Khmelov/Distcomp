public interface IService<T, TRequest, TResponse>
    where T : class
{
    Task<TResponse?> GetByIdAsync(long id);
    Task<IEnumerable<TResponse>> GetAllAsync();
    Task<TResponse> CreateAsync(TRequest request);
    Task<TResponse> UpdateAsync(long id, TRequest request);
    Task DeleteAsync(long id);
}