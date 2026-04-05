using Application.Dtos;
using Application.Interfaces;
using Domain.Models;
using Microsoft.EntityFrameworkCore;

namespace Infrastructure.Repositories;

public class LabelRepository : ILabelRepository
{
    private readonly BlogDbContext _context;

    public LabelRepository(BlogDbContext context)
    {
        _context = context;
    }

    public async Task<List<Label>> GetAllLabelsAsync()
    {
        return await _context.Labels.ToListAsync();
    }

    public async Task<Label?> GetLabelByIdAsync(long id)
    {
        return await _context
            .Labels
            .AsNoTracking()
            .FirstOrDefaultAsync(l => l.Id == id);
    }

    public async Task<Label> CreateLabelAsync(LabelCreateDto labelCreateDto)
    {
        var label = new Label()
        {
            Name = labelCreateDto.Name
        };

        await _context.Labels.AddAsync(label);
        await _context.SaveChangesAsync();

        return label;
    }

    public async Task DeleteLabelAsync(long id)
    {
        var label = await _context.Labels.FindAsync(id);

        _context.Labels.Remove(label!);
        await _context.SaveChangesAsync();
    }

    public async Task<Label> UpdateLabelAsync(LabelUpdateDto labelUpdateDto)
    {
        var label = await _context.Labels.FindAsync(labelUpdateDto.Id);

        label!.Name = labelUpdateDto.Name;

        await _context.SaveChangesAsync();

        return label;
    }
}
