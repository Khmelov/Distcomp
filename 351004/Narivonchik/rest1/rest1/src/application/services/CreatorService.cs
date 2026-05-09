using AutoMapper;
using rest1.application.DTOs.requests;
using rest1.application.DTOs.responses;
using rest1.application.exceptions;
using rest1.application.interfaces;
using rest1.application.interfaces.services;
using rest1.core.entities;
using RedisService.interfaces;

namespace rest1.application.services;

public class CreatorService : ICreatorService
{
    private readonly IMapper _mapper;
    private readonly ICreatorRepository _creatorRepository;
    private readonly IRedisCacheService _cacheService;
    private readonly string _cachePrefix;
    private readonly string _listCacheKey;

    public CreatorService(
        IMapper mapper, 
        ICreatorRepository repository,
        IRedisCacheService cacheService,
        IConfiguration configuration)
    {
        _mapper = mapper;
        _creatorRepository = repository;
        _cacheService = cacheService;
        _cachePrefix = configuration["Redis:CreatorCachePrefix"] ?? "creator:";
        _listCacheKey = configuration["Redis:CreatorsListCacheKey"] ?? "creators:all";
    }

    public async Task<CreatorResponseTo> CreateCreator(CreatorRequestTo creatorRequest)
    {
        if (creatorRequest.Id.HasValue)
        {
            var existingCreator = await _creatorRepository.GetByIdAsync(creatorRequest.Id.Value);
            if (existingCreator != null)
            {
                throw new CreatorAlreadyExistsException($"Creator with id {creatorRequest.Id} already exists");
            }
        }

        var creator = _mapper.Map<Creator>(creatorRequest);
    
        // Hash password with BCrypt
        creator.Password = BCrypt.Net.BCrypt.HashPassword(creatorRequest.Password);
        creator.Role = UserRole.CUSTOMER; // Default role
    
        var createdCreator = await _creatorRepository.AddAsync(creator);
    
        return _mapper.Map<CreatorResponseTo>(createdCreator);
    }

    public async Task DeleteCreator(CreatorRequestTo deleteCreatorRequestTo)
    {
        Creator creatorFromDto = _mapper.Map<Creator>(deleteCreatorRequestTo);

        _ = await _creatorRepository.DeleteAsync(creatorFromDto)
            ?? throw new CreatorNotFoundException(
                $"Delete creator {creatorFromDto} was not found");
        
        // Удаляем из кэша
        var cacheKey = $"{_cachePrefix}{creatorFromDto.Id}";
        await _cacheService.RemoveAsync(cacheKey);
        
        // Инвалидируем кэш списка
        await _cacheService.RemoveAsync(_listCacheKey);
        
        // Также удаляем все связанные новости из кэша
        await _cacheService.RemoveByPatternAsync("news:*");
    }

    public async Task<IEnumerable<CreatorResponseTo>> GetAllCreators()
    {
        // Пробуем получить из кэша
        return await _cacheService.GetOrSetAsync(_listCacheKey, async () =>
        {
            IEnumerable<Creator> allCreators = await _creatorRepository.GetAllAsync();
            var response = new List<CreatorResponseTo>();

            foreach (Creator creator in allCreators)
            {
                var creatorDto = _mapper.Map<CreatorResponseTo>(creator);
                response.Add(creatorDto);
                
                // Кэшируем каждый объект отдельно
                var cacheKey = $"{_cachePrefix}{creatorDto.Id}";
                await _cacheService.SetAsync(cacheKey, creatorDto);
            }

            return response;
        });
    }

    public async Task<CreatorResponseTo> GetCreator(CreatorRequestTo getCreatorRequestTo)
    {
        var cacheKey = $"{_cachePrefix}{getCreatorRequestTo.Id}";
        
        // Пробуем получить из кэша или из БД
        return await _cacheService.GetOrSetAsync(cacheKey, async () =>
        {
            Creator creatorFromDto = _mapper.Map<Creator>(getCreatorRequestTo);

            Creator demandedCreator =
                await _creatorRepository.GetByIdAsync(creatorFromDto.Id)
                ?? throw new CreatorNotFoundException(
                    $"Demanded creator {creatorFromDto} was not found");

            return _mapper.Map<CreatorResponseTo>(demandedCreator);
        });
    }

    public async Task<CreatorResponseTo> UpdateCreator(CreatorRequestTo updateCreatorRequestTo)
    {
        Creator creatorFromDto = _mapper.Map<Creator>(updateCreatorRequestTo);

        Creator updatedCreator =
            await _creatorRepository.UpdateAsync(creatorFromDto)
            ?? throw new CreatorNotFoundException(
                $"Update creator {creatorFromDto} was not found");

        var response = _mapper.Map<CreatorResponseTo>(updatedCreator);
        
        // Обновляем кэш
        var cacheKey = $"{_cachePrefix}{response.Id}";
        await _cacheService.SetAsync(cacheKey, response);
        
        // Инвалидируем кэш списка
        await _cacheService.RemoveAsync(_listCacheKey);
        
        // Также инвалидируем связанные новости
        await _cacheService.RemoveByPatternAsync($"news:*");

        return response;
    }
}