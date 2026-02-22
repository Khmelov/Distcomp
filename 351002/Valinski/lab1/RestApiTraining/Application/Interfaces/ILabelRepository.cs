using Application.Dtos;
using Domain.Models;

namespace Application.Interfaces;

public interface ILabelRepository
{
    Task<List<Label>> GetAllLabelsAsync();
    Task<Label?> GetLabelByIdAsync(long id);
    Task<Label> CreateLabelAsync(LabelCreateDto labelCreateDto);
    Task DeleteLabelAsync (long id);
    Task<Label> UpdateLabelAsync(LabelUpdateDto labelUpdateDto);

}
