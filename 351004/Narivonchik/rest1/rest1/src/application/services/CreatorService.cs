using AutoMapper;
using Microsoft.AspNetCore.Components.Forms;
using rest1.application.DTOs.requests;
using rest1.application.DTOs.responses;
using rest1.application.exceptions;
using rest1.application.interfaces;
using rest1.application.interfaces.services;
using rest1.core.entities;

namespace rest1.application.services;

public class CreatorService : ICreatorService
{
    private readonly IMapper _mapper;
    private readonly ICreatorRepository _creatorRepository;

    public CreatorService(IMapper mapper, ICreatorRepository repository)
    {
        _mapper = mapper;
        _creatorRepository = repository;
    }

    public async Task<CreatorResponseTo> CreateCreator(CreatorRequestTo createCreatorRequestTo)
    {
        Creator creatorFromDto = _mapper.Map<Creator>(createCreatorRequestTo);
 
        Creator createdCreator = await _creatorRepository.AddAsync(creatorFromDto);

        CreatorResponseTo dtoFromCreatedEditor = _mapper.Map<CreatorResponseTo>(createdCreator);

        return dtoFromCreatedEditor;
    }

    public async Task DeleteCreator(CreatorRequestTo deleteCreatorRequestTo)
    {
        Creator creatorFromDto = _mapper.Map<Creator>(deleteCreatorRequestTo);

        _ = await _creatorRepository.DeleteAsync(creatorFromDto)
            ?? throw new CreatorNotFoundException(
                $"Delete creator {creatorFromDto} was not found");
    }

    public async Task<IEnumerable<CreatorResponseTo>> GetAllCreators()
    {
        IEnumerable<Creator> allCreators =
            await _creatorRepository.GetAllAsync();

        var response = new List<CreatorResponseTo>();

        foreach (Creator creator in allCreators)
        {
            response.Add(_mapper.Map<CreatorResponseTo>(creator));
        }

        return response;
    }

    public async Task<CreatorResponseTo> GetCreator(CreatorRequestTo getCreatorRequestTo)
    {
        Creator creatorFromDto = _mapper.Map<Creator>(getCreatorRequestTo);

        Creator demandedCreator =
            await _creatorRepository.GetByIdAsync(creatorFromDto.Id)
            ?? throw new CreatorNotFoundException(
                $"Demanded creator {creatorFromDto} was not found");

        return _mapper.Map<CreatorResponseTo>(demandedCreator);
    }

    public async Task<CreatorResponseTo> UpdateCreator(CreatorRequestTo updateCreatorRequestTo)
    {
        Creator creatorFromDto = _mapper.Map<Creator>(updateCreatorRequestTo);

        Creator updatedCreator =
            await _creatorRepository.UpdateAsync(creatorFromDto)
            ?? throw new CreatorNotFoundException(
                $"Update creator {creatorFromDto} was not found");

        return _mapper.Map<CreatorResponseTo>(updatedCreator);
    }
}