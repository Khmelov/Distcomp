using Domain.Entities;

namespace Domain.Repositories;

public interface INoticeRepository : IRepositoryBase<Notice>
{
    Task<Notice?> FindNoticeByIdAsync(long id, bool trackChanges, CancellationToken cancellationToken = default);
}