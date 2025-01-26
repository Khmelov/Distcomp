using Domain.Entities;
using Domain.Repositories;

namespace Persistence.Repositories;

public class NoticeRepository : RepositoryBase<Notice>, INoticeRepository
{
    public NoticeRepository(RepositoryContext context) : base(context)
    {
    }
}