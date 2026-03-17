using AutoMapper;
using rest1.application.DTOs.requests;
using rest1.application.DTOs.responses;
using rest1.application.exceptions;
using rest1.application.interfaces;
using rest1.application.interfaces.services;
using rest1.core.entities;

namespace rest1.application.services;

public class MarkService : IMarkService
{
    private readonly IMapper _mapper;
    private readonly IMarkRepository _markRepository;

    public MarkService(IMapper mapper, IMarkRepository repository)
    {
        _mapper = mapper;
        _markRepository = repository;
    }

    public async Task<MarkResponseTo> CreateMark(MarkRequestTo createMarkRequestTo)
    {
        Mark markFromDto = _mapper.Map<Mark>(createMarkRequestTo);

        Mark createdMark = await _markRepository.AddAsync(markFromDto);

        MarkResponseTo dtoFromCreatedMark =
            _mapper.Map<MarkResponseTo>(createdMark);

        return dtoFromCreatedMark;
    }

    public async Task DeleteMark(MarkRequestTo deleteMarkRequestTo)
    {
        Mark markFromDto = _mapper.Map<Mark>(deleteMarkRequestTo);

        _ = await _markRepository.DeleteAsync(markFromDto)
            ?? throw new MarkNotFoundException(
                $"Delete mark {markFromDto} was not found");
    }

    public async Task<IEnumerable<MarkResponseTo>> GetAllMarks()
    {
        IEnumerable<Mark> allMarks =
            await _markRepository.GetAllAsync();

        var allMarksResponseTos = new List<MarkResponseTo>();

        foreach (Mark mark in allMarks)
        {
            MarkResponseTo markTo =
                _mapper.Map<MarkResponseTo>(mark);

            allMarksResponseTos.Add(markTo);
        }

        return allMarksResponseTos;
    }

    public async Task<MarkResponseTo> GetMark(MarkRequestTo getMarksRequestTo)
    {
        Mark markFromDto = _mapper.Map<Mark>(getMarksRequestTo);

        Mark demandedMark =
            await _markRepository.GetByIdAsync(markFromDto.Id)
            ?? throw new MarkNotFoundException(
                $"Demanded mark {markFromDto} was not found");

        MarkResponseTo demandedMarkResponseTo =
            _mapper.Map<MarkResponseTo>(demandedMark);

        return demandedMarkResponseTo;
    }

    public async Task<MarkResponseTo> UpdateMark(MarkRequestTo updateMarkRequestTo)
    {
        Mark markFromDto = _mapper.Map<Mark>(updateMarkRequestTo);

        Mark updateMark =
            await _markRepository.UpdateAsync(markFromDto)
            ?? throw new MarkNotFoundException(
                $"Update mark {markFromDto} was not found");

        MarkResponseTo updateMarkResponseTo =
            _mapper.Map<MarkResponseTo>(updateMark);

        return updateMarkResponseTo;
    }
}
