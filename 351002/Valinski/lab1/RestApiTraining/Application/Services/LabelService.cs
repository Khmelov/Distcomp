using Application.Abstractions;
using Application.Dtos;

namespace Application.Services;

public class LabelService : ILabelService
{
    public async Task<LabelGetDto> CreateLabelAsync(LabelCreateDto labelCreateDto)
    {
        throw new NotImplementedException();
    }

    public async Task<List<LabelGetDto>> GetAllLabelsAsync()
    {
        throw new NotImplementedException();
    }

    public async Task<LabelGetDto> GetLabelByIdAsync(long id)
    {
        throw new NotImplementedException();
    }

    public async Task<LabelGetDto> UpdateLabelAsync(LabelUpdateDto labelUpdateDto)
    {
        throw new NotImplementedException();
    }

    public async Task<bool> DeleteLabelAsync(long id)
    {
        throw new NotImplementedException();
    }
}
