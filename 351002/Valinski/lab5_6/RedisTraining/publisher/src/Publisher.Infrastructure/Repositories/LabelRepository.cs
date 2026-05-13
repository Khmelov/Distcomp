using Publisher.Application.Repositories;
using Publisher.Domain.Models;
using Publisher.Infrastructure.DbContexts;

namespace Publisher.Infrastructure.Repositories;

public class LabelRepository : GenericRepository<Label>, ILabelRepository
{
    public LabelRepository(PublisherDbContext context) : base(context)
    {
    }
}
