namespace External.Contracts.Interfaces;

public interface INoticeExternalService<TRequest, TResponse>
{
    Task<IEnumerable<TResponse>> GetAsync(CancellationToken cancellationToken = default);
    Task<TResponse> GetByIdAsync(long id, CancellationToken cancellationToken = default);
    Task<TResponse> CreateAsync(TRequest entity, CancellationToken cancellationToken = default);
    Task<TResponse> UpdateAsync(long id, TRequest entity, CancellationToken cancellationToken = default);
    Task DeleteAsync(long id, CancellationToken cancellationToken = default);
}