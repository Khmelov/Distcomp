using Microsoft.EntityFrameworkCore;
using RV_Kisel_lab2_Task320.Data;
using RV_Kisel_lab2_Task320.Models.Dtos;
using RV_Kisel_lab2_Task320.Models.Entities;

namespace RV_Kisel_lab2_Task320.Services;

public class CreatorService : ICreatorService {
    private readonly AppDbContext _context;
    public CreatorService(AppDbContext context) => _context = context;

    public async Task<IEnumerable<CreatorDto>> GetAllAsync() => 
        await _context.Creators.Select(c => new CreatorDto { Id = c.Id, Login = c.Login, Password = c.Password, Firstname = c.Firstname, Lastname = c.Lastname }).ToListAsync();

    public async Task<CreatorDto?> GetByIdAsync(int id) {
        var c = await _context.Creators.FindAsync(id);
        return c == null ? null : new CreatorDto { Id = c.Id, Login = c.Login, Password = c.Password, Firstname = c.Firstname, Lastname = c.Lastname };
    }

    public async Task<CreatorDto> CreateAsync(CreatorDto dto) {
        var c = new Creator { Login = dto.Login, Password = dto.Password, Firstname = dto.Firstname, Lastname = dto.Lastname };
        _context.Creators.Add(c); await _context.SaveChangesAsync();
        dto.Id = c.Id; return dto;
    }

    public async Task UpdateAsync(int id, CreatorDto dto) {
        var c = await _context.Creators.FindAsync(id);
        if (c != null) { c.Login = dto.Login; c.Password = dto.Password; c.Firstname = dto.Firstname; c.Lastname = dto.Lastname; await _context.SaveChangesAsync(); }
    }

    public async Task DeleteAsync(int id) {
        var c = await _context.Creators.FindAsync(id);
        if (c != null) { 
            _context.Creators.Remove(c); 
            await _context.SaveChangesAsync(); // Сначала удаляем создателя и его новости

            // МАГИЯ: Удаляем "ничейные" метки
            var orphanedLabels = await _context.Labels.Where(l => !l.News.Any()).ToListAsync();
            if (orphanedLabels.Any()) {
                _context.Labels.RemoveRange(orphanedLabels);
                await _context.SaveChangesAsync();
            }
        }
    }
}