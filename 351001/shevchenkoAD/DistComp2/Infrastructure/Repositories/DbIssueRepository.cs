using Domain.Entities;
using Domain.Interfaces;
using Infrastructure.Abstractions;
using Microsoft.EntityFrameworkCore;

namespace Infrastructure.Repositories;

public class DbIssueRepository : DbBaseRepository<Issue>, IIssueRepository
{
    public DbIssueRepository(AppDbContext context) : base(context) { }
    
    public async Task<Issue?> GetByIdWithLabelsAsync(long id)
    {
        return await _dbSet
                   .Include(i => i.Labels)
                   .FirstOrDefaultAsync(i => i.Id == id);
    }

    public async Task<bool> IsLabelUsedAsync(long labelId)
    {
        return await _dbSet.AnyAsync(i => i.Labels.Any(l => l.Id == labelId));
    }
}