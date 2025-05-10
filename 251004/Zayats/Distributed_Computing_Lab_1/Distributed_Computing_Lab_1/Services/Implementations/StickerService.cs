using AutoMapper;
using Distributed_Computing_Lab_1.DTO.RequestDTO;
using Distributed_Computing_Lab_1.DTO.ResponseDTO;
using Distributed_Computing_Lab_1.Exceptions;
using Distributed_Computing_Lab_1.Infrastructure.Validators;
using Distributed_Computing_Lab_1.Models;
using Distributed_Computing_Lab_1.Repositories.Interfaces;
using Distributed_Computing_Lab_1.Services.Interfaces;
using FluentValidation;

namespace Distributed_Computing_Lab_1.Services.Implementations;

public class StickerService : IStickerService
{
    private readonly IStickerRepository _stickerRepository;
    private readonly IMapper _mapper;
    private readonly StickerRequestDTOValidator _validator;
    
    public StickerService(IStickerRepository stickerRepository, 
        IMapper mapper, StickerRequestDTOValidator validator)
    {
        _stickerRepository = stickerRepository;
        _mapper = mapper;
        _validator = validator;
    }
    
    public async Task<IEnumerable<StickerResponseDTO>> GetTagsAsync()
    {
        var tags = await _stickerRepository.GetAllAsync();
        return _mapper.Map<IEnumerable<StickerResponseDTO>>(tags);
    }

    public async Task<StickerResponseDTO> GetTagByIdAsync(long id)
    {
        var tag = await _stickerRepository.GetByIdAsync(id)
                  ?? throw new NotFoundException(ErrorCodes.TagNotFound, ErrorMessages.TagNotFoundMessage(id));
        return _mapper.Map<StickerResponseDTO>(tag);
    }

    public async Task<StickerResponseDTO> CreateTagAsync(StickerRequestDTO sticker)
    {
        await _validator.ValidateAndThrowAsync(sticker);
        var tagToCreate = _mapper.Map<Sticker>(sticker);
        var createdTag = await _stickerRepository.CreateAsync(tagToCreate);
        return _mapper.Map<StickerResponseDTO>(createdTag);
    }

    public async Task<StickerResponseDTO> UpdateTagAsync(StickerRequestDTO sticker)
    {
        await _validator.ValidateAndThrowAsync(sticker);
        var tagToUpdate = _mapper.Map<Sticker>(sticker);
        var updatedTag = await _stickerRepository.UpdateAsync(tagToUpdate)
                         ?? throw new NotFoundException(ErrorCodes.TagNotFound, ErrorMessages.TagNotFoundMessage(sticker.Id));
        return _mapper.Map<StickerResponseDTO>(updatedTag);
    }

    public async Task DeleteTagAsync(long id)
    {
        if (await _stickerRepository.DeleteAsync(id) is null)
        {
            throw new NotFoundException(ErrorCodes.TagNotFound, ErrorMessages.TagNotFoundMessage(id));
        }
    }
}