using AutoMapper;
using rest1.application.DTOs.requests;
using rest1.application.DTOs.responses;
using rest1.application.exceptions;
using rest1.application.exceptions.db;
using rest1.application.interfaces;
using rest1.application.interfaces.services;
using rest1.core.entities;
using RedisService.interfaces;
using Microsoft.Extensions.Configuration;

namespace rest1.application.services;

public class NewsService : INewsService
{
    private readonly IMapper _mapper;
    private readonly INewsRepository _newsRepository;
    private readonly IMarkRepository _markRepository;
    private readonly IRedisCacheService _cacheService;
    private readonly string _cachePrefix;
    private readonly string _listCacheKey;

    public NewsService(
        IMapper mapper, 
        INewsRepository repository,  
        IMarkRepository markRepository,
        IRedisCacheService cacheService,
        IConfiguration configuration)
    {
        _mapper = mapper;
        _newsRepository = repository;
        _markRepository = markRepository;
        _cacheService = cacheService;
        _cachePrefix = configuration["Redis:NewsCachePrefix"] ?? "news:";
        _listCacheKey = configuration["Redis:NewsListCacheKey"] ?? "news:all";
    }

    public async Task<NewsResponseTo> CreateNews(NewsRequestTo createNewsRequestTo)
    {
        News newsFromDto = _mapper.Map<News>(createNewsRequestTo);

        try
        {
            News createdNews = await _newsRepository.AddAsync(newsFromDto);
            NewsResponseTo dtoFromCreatedNews = _mapper.Map<NewsResponseTo>(createdNews);
            
            // Кэшируем созданный объект
            var cacheKey = $"{_cachePrefix}{dtoFromCreatedNews.Id}";
            await _cacheService.SetAsync(cacheKey, dtoFromCreatedNews);
            
            // Инвалидируем кэш списка новостей
            await _cacheService.RemoveAsync(_listCacheKey);
            
            // Инвалидируем кэш списка новостей для конкретного создателя
            await _cacheService.RemoveAsync($"{_listCacheKey}:creator:{createdNews.CreatorId}");
            
            return dtoFromCreatedNews;
        }
        catch (InvalidOperationException ex)
        {
            throw new NewsAlreadyExistsException(ex.Message, ex);
        }
        catch (ForeignKeyViolationException ex)
        {
            throw new NewsReferenceException(ex.Message, ex);
        }
    }

    public async Task DeleteNews(NewsRequestTo deleteNewsRequestTo)
    {
        News newsFromDto = _mapper.Map<News>(deleteNewsRequestTo);
        
        // Получаем новость перед удалением для инвалидации кэша
        var news = await _newsRepository.GetByIdAsync(newsFromDto.Id);
        
        _ = await _newsRepository.DeleteAsync(newsFromDto) 
            ?? throw new NewNotFoundException($"Deleted news {newsFromDto} was not found");
        
        await _markRepository.DeleteMarksWithoutNews();
        
        // Удаляем из кэша
        var cacheKey = $"{_cachePrefix}{newsFromDto.Id}";
        await _cacheService.RemoveAsync(cacheKey);
        
        // Инвалидируем кэш списков
        await _cacheService.RemoveAsync(_listCacheKey);
        if (news != null)
        {
            await _cacheService.RemoveAsync($"{_listCacheKey}:creator:{news.CreatorId}");
        }
        
        // Инвалидируем кэш меток
        await _cacheService.RemoveByPatternAsync("mark:*");
    }

    public async Task<IEnumerable<NewsResponseTo>> GetAllNews()
    {
        // Пробуем получить из кэша
        return await _cacheService.GetOrSetAsync(_listCacheKey, async () =>
        {
            IEnumerable<News> allNews = await _newsRepository.GetAllAsync();
            
            var allNewsResponseTos = new List<NewsResponseTo>();
            foreach (News news in allNews)
            {
                NewsResponseTo newsTo = _mapper.Map<NewsResponseTo>(news);
                allNewsResponseTos.Add(newsTo);
                
                // Кэшируем каждый объект отдельно
                var cacheKey = $"{_cachePrefix}{newsTo.Id}";
                await _cacheService.SetAsync(cacheKey, newsTo);
            }

            return allNewsResponseTos;
        });
    }

    public async Task<NewsResponseTo> GetNews(NewsRequestTo getNewsRequestTo)
    {
        var cacheKey = $"{_cachePrefix}{getNewsRequestTo.Id}";
        
        // Пробуем получить из кэша или из БД
        return await _cacheService.GetOrSetAsync(cacheKey, async () =>
        {
            News newsFromDto = _mapper.Map<News>(getNewsRequestTo);

            News demandedNews = await _newsRepository.GetByIdAsync(newsFromDto.Id) 
                ?? throw new NewNotFoundException($"Demanded news {newsFromDto} was not found");

            return _mapper.Map<NewsResponseTo>(demandedNews);
        });
    }

    public async Task<NewsResponseTo> UpdateNews(NewsRequestTo updateNewsRequestTo)
    {
        News newsFromDto = _mapper.Map<News>(updateNewsRequestTo);

        News? updateNews = await _newsRepository.UpdateAsync(newsFromDto) 
            ?? throw new NewNotFoundException($"Update news {newsFromDto} was not found");

        NewsResponseTo updateNewsResponseTo = _mapper.Map<NewsResponseTo>(updateNews);
        
        // Обновляем кэш
        var cacheKey = $"{_cachePrefix}{updateNewsResponseTo.Id}";
        await _cacheService.SetAsync(cacheKey, updateNewsResponseTo);
        
        // Инвалидируем кэш списков
        await _cacheService.RemoveAsync(_listCacheKey);
        await _cacheService.RemoveAsync($"{_listCacheKey}:creator:{updateNews.CreatorId}");

        return updateNewsResponseTo;
    }
}