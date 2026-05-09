using Microsoft.EntityFrameworkCore;
using rest1.application.interfaces;
using rest1.core.entities;

namespace rest1.persistence.db.repositories;

public class DbMarkRepository : DbRepository<Mark>, IMarkRepository
{
    public DbMarkRepository(RestServiceDbContext dbContext) : base(dbContext)
    {
    }

    public async Task DeleteMarksWithoutNews()
    {
        // var emptyMarkers = await _dbSet.Where(m => m.News.Count == 0).ToListAsync();
        // _dbSet.RemoveRange(emptyMarkers);
        // await _dbContext.SaveChangesAsync();
    }
}
