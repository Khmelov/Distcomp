using AutoMapper;
using rest1.application.DTOs.requests;
using rest1.application.DTOs.responses;
using rest1.application.exceptions;
using rest1.application.interfaces;
using rest1.application.interfaces.services;
using rest1.core.entities;
using RedisService.interfaces;
using Microsoft.Extensions.Configuration;

namespace rest1.application.services;

public class MarkService : IMarkService
{
    private readonly IMapper _mapper;
    private readonly IMarkRepository _markRepository;
    private readonly IRedisCacheService _cacheService;
    private readonly string _cachePrefix;
    private readonly string _listCacheKey;

    public MarkService(
        IMapper mapper, 
        IMarkRepository repository,
        IRedisCacheService cacheService,
        IConfiguration configuration)
    {
        _mapper = mapper;
        _markRepository = repository;
        _cacheService = cacheService;
        _cachePrefix = configuration["Redis:MarkCachePrefix"] ?? "mark:";
        _listCacheKey = configuration["Redis:MarksListCacheKey"] ?? "marks:all";
    }

    public async Task<MarkResponseTo> CreateMark(MarkRequestTo createMarkRequestTo)
    {
        Mark markFromDto = _mapper.Map<Mark>(createMarkRequestTo);

        try
        {
            Mark createdMark = await _markRepository.AddAsync(markFromDto);
            MarkResponseTo dtoFromCreatedMark = _mapper.Map<MarkResponseTo>(createdMark);
            
            // Кэшируем созданный объект
            var cacheKey = $"{_cachePrefix}{dtoFromCreatedMark.Id}";
            await _cacheService.SetAsync(cacheKey, dtoFromCreatedMark);
            
            // Инвалидируем кэш списков
            await _cacheService.RemoveAsync(_listCacheKey);
            await _cacheService.RemoveAsync($"{_listCacheKey}:news:{createdMark.Id}");
            
            return dtoFromCreatedMark;
        }
        catch (InvalidOperationException ex)
        {
            throw new MarkAlreadyExistsException(ex.Message, ex);
        }
    }

    public async Task DeleteMark(MarkRequestTo deleteMarkRequestTo)
    {
        Mark markFromDto = _mapper.Map<Mark>(deleteMarkRequestTo);
        
        // Получаем метку перед удалением
        var mark = await _markRepository.GetByIdAsync(markFromDto.Id);

        _ = await _markRepository.DeleteAsync(markFromDto)
            ?? throw new MarkNotFoundException(
                $"Delete mark {markFromDto} was not found");
        
        // Удаляем из кэша
        var cacheKey = $"{_cachePrefix}{markFromDto.Id}";
        await _cacheService.RemoveAsync(cacheKey);
        
        // Инвалидируем кэш списков
        await _cacheService.RemoveAsync(_listCacheKey);
        if (mark != null)
        {
            await _cacheService.RemoveAsync($"{_listCacheKey}:news:{mark.Id}");
        }
    }

    public async Task<IEnumerable<MarkResponseTo>> GetAllMarks()
    {
        // Пробуем получить из кэша
        return await _cacheService.GetOrSetAsync(_listCacheKey, async () =>
        {
            IEnumerable<Mark> allMarks = await _markRepository.GetAllAsync();

            var allMarksResponseTos = new List<MarkResponseTo>();

            foreach (Mark mark in allMarks)
            {
                MarkResponseTo markTo = _mapper.Map<MarkResponseTo>(mark);
                allMarksResponseTos.Add(markTo);
                
                // Кэшируем каждый объект отдельно
                var cacheKey = $"{_cachePrefix}{markTo.Id}";
                await _cacheService.SetAsync(cacheKey, markTo);
            }

            return allMarksResponseTos;
        });
    }

    public async Task<MarkResponseTo> GetMark(MarkRequestTo getMarksRequestTo)
    {
        var cacheKey = $"{_cachePrefix}{getMarksRequestTo.Id}";
        
        // Пробуем получить из кэша или из БД
        return await _cacheService.GetOrSetAsync(cacheKey, async () =>
        {
            Mark markFromDto = _mapper.Map<Mark>(getMarksRequestTo);

            Mark demandedMark =
                await _markRepository.GetByIdAsync(markFromDto.Id)
                ?? throw new MarkNotFoundException(
                    $"Demanded mark {markFromDto} was not found");

            return _mapper.Map<MarkResponseTo>(demandedMark);
        });
    }

    public async Task<MarkResponseTo> UpdateMark(MarkRequestTo updateMarkRequestTo)
    {
        Mark markFromDto = _mapper.Map<Mark>(updateMarkRequestTo);

        Mark updateMark =
            await _markRepository.UpdateAsync(markFromDto)
            ?? throw new MarkNotFoundException(
                $"Update mark {markFromDto} was not found");

        MarkResponseTo updateMarkResponseTo = _mapper.Map<MarkResponseTo>(updateMark);
        
        // Обновляем кэш
        var cacheKey = $"{_cachePrefix}{updateMarkResponseTo.Id}";
        await _cacheService.SetAsync(cacheKey, updateMarkResponseTo);
        
        // Инвалидируем кэш списков
        await _cacheService.RemoveAsync(_listCacheKey);
        await _cacheService.RemoveAsync($"{_listCacheKey}:news:{updateMark.Id}");

        return updateMarkResponseTo;
    }
}