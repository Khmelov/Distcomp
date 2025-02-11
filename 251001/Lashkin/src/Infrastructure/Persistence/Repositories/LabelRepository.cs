using Domain.Entities;
using Domain.Repositories;
using Microsoft.EntityFrameworkCore;

namespace Persistence.Repositories;

public class LabelRepository : RepositoryBase<Label>, ILabelRepository
{
    public LabelRepository(RepositoryContext context) : base(context)
    {
    }

    public async Task<Label?> FindLabelByIdAsync(long id, bool trackChanges, CancellationToken cancellationToken = default)
    {
        var label = await FindByCondition(label => label.Id == id, trackChanges).SingleOrDefaultAsync(cancellationToken);
        
        return label;
    }
}