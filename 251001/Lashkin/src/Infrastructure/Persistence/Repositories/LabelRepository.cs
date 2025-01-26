using Domain.Entities;
using Domain.Repositories;

namespace Persistence.Repositories;

public class LabelRepository : RepositoryBase<Label>, ILabelRepository
{
    public LabelRepository(RepositoryContext context) : base(context)
    {
    }
}