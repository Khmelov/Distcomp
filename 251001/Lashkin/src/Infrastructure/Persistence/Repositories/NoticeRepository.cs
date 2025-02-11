using Domain.Entities;
using Domain.Repositories;
using Microsoft.EntityFrameworkCore;

namespace Persistence.Repositories;

public class NoticeRepository : RepositoryBase<Notice>, INoticeRepository
{
    public NoticeRepository(RepositoryContext context) : base(context)
    {
    }
    
    public async Task<Notice?> FindNoticeByIdAsync(long id, bool trackChanges, CancellationToken cancellationToken = default)
    {
        var notice = await FindByCondition(notice => notice.Id == id, trackChanges).SingleOrDefaultAsync(cancellationToken);
        
        return notice;
    }
}