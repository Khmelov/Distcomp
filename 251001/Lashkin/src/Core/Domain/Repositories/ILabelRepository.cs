using Domain.Entities;

namespace Domain.Repositories;

public interface ILabelRepository : IRepositoryBase<Label>
{
    Task<Label?> FindLabelByIdAsync(long id, bool trackChanges, CancellationToken cancellationToken = default);
}