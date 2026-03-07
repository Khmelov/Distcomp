using Microsoft.EntityFrameworkCore;
using RV_Kisel_lab2_Task320.Data;
using RV_Kisel_lab2_Task320.Models.Dtos;
using RV_Kisel_lab2_Task320.Models.Entities;

namespace RV_Kisel_lab2_Task320.Services;

public class LabelService : ILabelService
{
    private readonly AppDbContext _context;
    public LabelService(AppDbContext context) => _context = context;

    public async Task<IEnumerable<LabelDto>> GetAllAsync() =>
        await _context.Labels.Select(l => new LabelDto { Id = l.Id, Name = l.Name }).ToListAsync();

    public async Task<LabelDto?> GetByIdAsync(int id)
    {
        var l = await _context.Labels.FindAsync(id);
        return l == null ? null : new LabelDto { Id = l.Id, Name = l.Name };
    }

    public async Task<LabelDto> CreateAsync(LabelDto dto)
    {
        var label = new Label { Name = dto.Name };
        _context.Labels.Add(label);
        await _context.SaveChangesAsync();
        dto.Id = label.Id;
        return dto;
    }

    public async Task UpdateAsync(int id, LabelDto dto)
    {
        var l = await _context.Labels.FindAsync(id);
        if (l != null) { l.Name = dto.Name; await _context.SaveChangesAsync(); }
    }

    public async Task DeleteAsync(int id)
    {
        // 1. Сначала 100% точно проверяем, используется ли метка хоть в одной новости
        bool isInUse = await _context.News.AnyAsync(n => n.Labels.Any(l => l.Id == id));
        
        // 2. Если используется - бросаем ошибку (которая превратится в статус 400)
        if (isInUse) 
        {
            throw new InvalidOperationException("Label is in use");
        }

        // 3. Если не используется - спокойно удаляем
        var label = await _context.Labels.FindAsync(id);
        if (label != null) { 
            _context.Labels.Remove(label); 
            await _context.SaveChangesAsync(); 
        }
    }
}