using Microsoft.EntityFrameworkCore;
using RV_Kisel_lab2_Task320.Data;
using RV_Kisel_lab2_Task320.Models.Dtos;
using RV_Kisel_lab2_Task320.Models.Entities;

namespace RV_Kisel_lab2_Task320.Services;

public class NewsService : INewsService
{
    private readonly AppDbContext _context;
    public NewsService(AppDbContext context) => _context = context;

    public async Task<IEnumerable<NewsDto>> GetAllAsync() =>
        await _context.News.Include(n => n.Labels).Select(n => new NewsDto {
            Id = n.Id, Title = n.Title, Content = n.Content, CreatorId = n.CreatorId,
            Labels = n.Labels.Select(l => new LabelDto { Id = l.Id, Name = l.Name }).ToList()
        }).ToListAsync();

    public async Task<NewsDto?> GetByIdAsync(int id)
    {
        var n = await _context.News.Include(x => x.Labels).FirstOrDefaultAsync(x => x.Id == id);
        return n == null ? null : new NewsDto {
            Id = n.Id, Title = n.Title, Content = n.Content, CreatorId = n.CreatorId,
            Labels = n.Labels.Select(l => new LabelDto { Id = l.Id, Name = l.Name }).ToList()
        };
    }

    public async Task<NewsDto> CreateAsync(NewsDto dto)
    {
        var news = new News { Title = dto.Title, Content = dto.Content, CreatorId = dto.CreatorId };
        
        if (dto.Labels != null && dto.Labels.Any()) 
        {
            var labelNames = dto.Labels.Select(l => l.Name).ToList();
            var existingLabels = await _context.Labels.Where(l => labelNames.Contains(l.Name)).ToListAsync();
            var existingNames = existingLabels.Select(e => e.Name).ToList();
            
            var missingNames = labelNames.Except(existingNames).ToList();
            foreach(var missing in missingNames) {
                var newLabel = new Label { Name = missing };
                _context.Labels.Add(newLabel);
                existingLabels.Add(newLabel);
            }
            news.Labels = existingLabels;
        }

        _context.News.Add(news);
        await _context.SaveChangesAsync();
        
        dto.Id = news.Id;
        dto.Labels = news.Labels.Select(l => new LabelDto { Id = l.Id, Name = l.Name }).ToList();
        return dto;
    }

    public async Task UpdateAsync(int id, NewsDto dto)
    {
        var n = await _context.News.Include(x => x.Labels).FirstOrDefaultAsync(x => x.Id == id);
        if (n != null)
        {
            n.Title = dto.Title; n.Content = dto.Content; n.CreatorId = dto.CreatorId;
            
            if (dto.Labels != null) 
            {
                var labelNames = dto.Labels.Select(l => l.Name).ToList();
                var existingLabels = await _context.Labels.Where(l => labelNames.Contains(l.Name)).ToListAsync();
                var existingNames = existingLabels.Select(e => e.Name).ToList();
                
                var missingNames = labelNames.Except(existingNames).ToList();
                foreach(var missing in missingNames) {
                    var newLabel = new Label { Name = missing };
                    _context.Labels.Add(newLabel);
                    existingLabels.Add(newLabel);
                }
                
                n.Labels.Clear(); 
                foreach (var l in existingLabels) n.Labels.Add(l);
            }

            await _context.SaveChangesAsync();

            // МАГИЯ: Удаляем "ничейные" метки
            var orphanedLabels = await _context.Labels.Where(l => !l.News.Any()).ToListAsync();
            if (orphanedLabels.Any()) {
                _context.Labels.RemoveRange(orphanedLabels);
                await _context.SaveChangesAsync();
            }
        }
    }

    public async Task DeleteAsync(int id)
    {
        var n = await _context.News.FindAsync(id);
        if (n != null) { 
            _context.News.Remove(n); 
            await _context.SaveChangesAsync(); 

            // МАГИЯ: Удаляем "ничейные" метки
            var orphanedLabels = await _context.Labels.Where(l => !l.News.Any()).ToListAsync();
            if (orphanedLabels.Any()) {
                _context.Labels.RemoveRange(orphanedLabels);
                await _context.SaveChangesAsync();
            }
        }
    }

    public async Task<bool> ExistsByTitleAsync(string title) => 
        await _context.News.AnyAsync(n => n.Title == title);
}