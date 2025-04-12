using Domain.Entities;
using Domain.Interfaces;

namespace Persistence.Repositories;

public class NoticeRepository : RepositoryBase<Notice>, INoticeRepository
{
    public NoticeRepository(RepositoryContext context) : base(context)
    {
    }
}