using Domain.Entities;

namespace Domain.Repositories;

public interface INewsRepository : IRepositoryBase<News>
{
    Task<News?> FindNewsByIdAsync(long id, bool trackChanges, CancellationToken cancellationToken = default);
}