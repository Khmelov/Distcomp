using System.Linq.Expressions;
using lab1.Common.Paging;
using lab1.Data;
using lab1.Models.Entities;
using lab1.Repositories.Interfaces;
using Microsoft.EntityFrameworkCore;

namespace lab1.Repositories.Ef;

public class IssueEfRepository : EfEntityRepository<Issue>, IIssueRepository
{
    public IssueEfRepository(AppDbContext db)
        : base(db)
    {
    }

    public override Task<Issue?> GetByIdAsync(long id, CancellationToken cancellationToken = default)
        => Db.Issues
            .Include(i => i.Labels)
            .FirstOrDefaultAsync(i => i.Id == id, cancellationToken);

    public override async Task<IReadOnlyList<Issue>> GetAllAsync(CancellationToken cancellationToken = default)
    {
        return await Db.Issues
            .Include(i => i.Labels)
            .AsNoTracking()
            .OrderBy(i => i.Id)
            .ToListAsync(cancellationToken);
    }

    public override async Task<PagedResult<Issue>> GetPagedAsync(
        Expression<Func<Issue, bool>>? filter,
        Func<IQueryable<Issue>, IOrderedQueryable<Issue>>? orderBy,
        int page,
        int size,
        CancellationToken cancellationToken = default)
    {
        var safeSize = size <= 0 ? 20 : Math.Min(size, 200);
        var safePage = page < 0 ? 0 : page;

        IQueryable<Issue> query = Db.Issues.Include(i => i.Labels).AsNoTracking();
        if (filter != null)
            query = query.Where(filter);

        var total = await query.CountAsync(cancellationToken);

        query = orderBy != null ? orderBy(query) : query.OrderBy(i => i.Id);

        var items = await query
            .Skip(safePage * safeSize)
            .Take(safeSize)
            .ToListAsync(cancellationToken);

        return PagedResult<Issue>.Create(items, total, safePage, safeSize);
    }

    public override async Task DeleteByIdAsync(long id, CancellationToken cancellationToken = default)
    {
        var issue = await Db.Issues.FirstOrDefaultAsync(i => i.Id == id, cancellationToken);
        if (issue == null)
            throw new KeyNotFoundException("Issue not found");

        Db.Issues.Remove(issue);
        await Db.SaveChangesAsync(cancellationToken);
    }
}
