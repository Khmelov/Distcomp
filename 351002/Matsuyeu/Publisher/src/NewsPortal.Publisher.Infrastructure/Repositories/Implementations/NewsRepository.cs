// Data/Repositories/NewsRepository.cs
using Microsoft.EntityFrameworkCore;
using Publisher.src.NewsPortal.Publisher.Domain.Entities;
using Publisher.src.NewsPortal.Publisher.Infrastructure.Data;

namespace Publisher.src.NewsPortal.Publisher.Infrastructure.Repositories.Implementations
{
    public class NewsRepository : GenericRepository<News>
    {
        public NewsRepository(PostgresDbContext context) : base(context)
        {
        }

        public override async Task<News> AddAsync(News entity)
        {
            await _dbSet.AddAsync(entity);
            await _context.SaveChangesAsync();

            // Перезагружаем новость с метками
            return await _dbSet
                .Include(n => n.Creator)
                .Include(n => n.Marks)
                .FirstOrDefaultAsync(n => n.Id == entity.Id);
        }

        public override async Task<News?> GetByIdAsync(long id)
        {
            return await _dbSet
                .Include(n => n.Creator)
                .Include(n => n.Marks)
                .FirstOrDefaultAsync(n => n.Id == id);
        }

        public override async Task<IEnumerable<News>> GetAllAsync()
        {
            return await _dbSet
                .Include(n => n.Creator)
                .Include(n => n.Marks)
                .ToListAsync();
        }

        public override async Task UpdateAsync(News entity)
        {
            // Загружаем существующую новость с метками
            var existingNews = await _dbSet
                .Include(n => n.Marks)
                .FirstOrDefaultAsync(n => n.Id == entity.Id);

            if (existingNews != null)
            {
                // Обновляем поля
                _context.Entry(existingNews).CurrentValues.SetValues(entity);

                // Обновляем метки
                existingNews.Marks.Clear();
                foreach (var mark in entity.Marks)
                {
                    var trackedMark = await _context.Marks.FindAsync(mark.Id);
                    if (trackedMark != null)
                    {
                        existingNews.Marks.Add(trackedMark);
                    }
                }

                await _context.SaveChangesAsync();
            }
        }
    }
}