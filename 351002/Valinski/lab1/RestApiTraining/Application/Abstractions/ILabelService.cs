using Application.Dtos;

namespace Application.Abstractions;

public interface ILabelService
{
    Task<LabelGetDto> CreateLabelAsync(LabelCreateDto labelCreateDto);
    Task<List<LabelGetDto>> GetAllLabelsAsync();
    Task<LabelGetDto> GetLabelByIdAsync(long id);
    Task<LabelGetDto> UpdateLabelAsync(LabelUpdateDto labelUpdateDto);
    Task<bool> DeleteLabelAsync(long id);
}

