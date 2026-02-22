using Application.Abstractions;
using Application.Dtos;
using Application.Interfaces;
using AutoMapper;

namespace Application.Services;

public class LabelService : ILabelService
{
    private readonly ILabelRepository _labelRepository;
    private readonly IMapper _mapper;

    public LabelService(ILabelRepository labelRepository, IMapper mapper)
    {
        _labelRepository = labelRepository;
        _mapper = mapper;
    }

    public async Task<LabelGetDto> CreateLabelAsync(LabelCreateDto labelCreateDto)
    {
        var label = await _labelRepository.CreateLabelAsync(labelCreateDto);
        return _mapper.Map<LabelGetDto>(label);
    }

    public async Task<List<LabelGetDto>> GetAllLabelsAsync()
    {
        return _mapper.Map<List<LabelGetDto>>(await _labelRepository.GetAllLabelsAsync());
    }

    public async Task<LabelGetDto> GetLabelByIdAsync(long id)
    {
        var label = await _labelRepository.GetLabelByIdAsync(id);
        return _mapper.Map<LabelGetDto>(label);
    }

    public async Task<LabelGetDto> UpdateLabelAsync(LabelUpdateDto labelUpdateDto)
    {
        if (await _labelRepository.GetLabelByIdAsync(labelUpdateDto.Id) == null)
        {
            return null;
        }

        var updated = await _labelRepository.UpdateLabelAsync(labelUpdateDto);
        return _mapper.Map<LabelGetDto>(updated);
    }

    public async Task<bool> DeleteLabelAsync(long id)
    {
        if (await _labelRepository.GetLabelByIdAsync(id) == null)
        {
            return false;
        }

        await _labelRepository.DeleteLabelAsync(id);
        return true;
    }
}
