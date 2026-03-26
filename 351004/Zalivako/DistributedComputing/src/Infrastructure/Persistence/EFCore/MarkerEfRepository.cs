using Application.Interfaces;
using Core.Entities;
using Microsoft.EntityFrameworkCore;

namespace Infrastructure.Persistence.EFCore
{
    public class MarkerEfRepository : EfRepository<Marker>, IMarkerRepository
    {
        public MarkerEfRepository(AppDbContext dbContext) : base(dbContext)
        {
        }

        public async Task DeleteMarkersWithoutNews()
        {
            //var emptyMarkers = await _dbSet.Where(m => m.News.Count == 0).ToListAsync();
            //_dbSet.RemoveRange(emptyMarkers);
            //await _dbContext.SaveChangesAsync();
        }
    }
}