using Application.DTOs.Requests;
using Application.DTOs.Responses;
using Application.Exceptions.Application;
using Application.Exceptions.Persistance;
using Application.Interfaces;
using AutoMapper;
using Core.Entities;
using Microsoft.Extensions.Caching.Distributed;
using System.Text.Json;

namespace Application.Services
{
    public class NewsService : INewsService
    {
        private readonly IMapper _mapper;
        private readonly INewsRepository _newsRepository;
        private readonly IMarkerRepository _markerRepository;
        private readonly IDistributedCache _cache;

        private const string AllNewsCacheKey = "news_all";
        private static string NewsByIdKey(long id) => $"news_{id}";

        public NewsService(IMapper mapper, INewsRepository newsRepository,
                           IMarkerRepository markerRepository, IDistributedCache cache)
        {
            _mapper = mapper;
            _newsRepository = newsRepository;
            _markerRepository = markerRepository;
            _cache = cache;
        }

        public async Task<NewsResponseTo> CreateNews(NewsRequestTo createNewsRequestTo)
        {
            News news = _mapper.Map<News>(createNewsRequestTo);
            try
            {
                News created = await _newsRepository.AddAsync(news);
                NewsResponseTo response = _mapper.Map<NewsResponseTo>(created);
                await InvalidateCacheAsync(created.Id);
                return response;
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

        public async Task<IEnumerable<NewsResponseTo>> GetAllNews()
        {
            var cached = await _cache.GetStringAsync(AllNewsCacheKey);
            if (!string.IsNullOrEmpty(cached))
                return JsonSerializer.Deserialize<List<NewsResponseTo>>(cached)!;

            IEnumerable<News> newsList = await _newsRepository.GetAllAsync();
            var result = _mapper.Map<List<NewsResponseTo>>(newsList);

            var options = new DistributedCacheEntryOptions()
                .SetAbsoluteExpiration(TimeSpan.FromMinutes(5));
            await _cache.SetStringAsync(AllNewsCacheKey, JsonSerializer.Serialize(result), options);
            return result;
        }

        public async Task<NewsResponseTo> GetNews(NewsRequestTo getNewsRequestTo)
        {
            long id = getNewsRequestTo.Id ?? 0;
            string cacheKey = NewsByIdKey(id);
            var cached = await _cache.GetStringAsync(cacheKey);
            if (!string.IsNullOrEmpty(cached))
                return JsonSerializer.Deserialize<NewsResponseTo>(cached);

            News news = await _newsRepository.GetByIdAsync(id)
                ?? throw new NewNotFoundException($"News {id} not found");
            NewsResponseTo response = _mapper.Map<NewsResponseTo>(news);

            var options = new DistributedCacheEntryOptions()
                .SetAbsoluteExpiration(TimeSpan.FromMinutes(5));
            await _cache.SetStringAsync(cacheKey, JsonSerializer.Serialize(response), options);
            return response;
        }

        public async Task<NewsResponseTo> UpdateNews(NewsRequestTo updateNewsRequestTo)
        {
            News news = _mapper.Map<News>(updateNewsRequestTo);
            News? updated = await _newsRepository.UpdateAsync(news)
                ?? throw new NewNotFoundException($"News not found for update");
            NewsResponseTo response = _mapper.Map<NewsResponseTo>(updated);
            await InvalidateCacheAsync(updated.Id);
            return response;
        }

        public async Task DeleteNews(NewsRequestTo deleteNewsRequestTo)
        {
            News news = _mapper.Map<News>(deleteNewsRequestTo);
            await _newsRepository.DeleteAsync(news);
            await _markerRepository.DeleteMarkersWithoutNews();
            await InvalidateCacheAsync(news.Id);
        }

        private async Task InvalidateCacheAsync(long newsId)
        {
            await _cache.RemoveAsync(AllNewsCacheKey);
            await _cache.RemoveAsync(NewsByIdKey(newsId));
        }
    }
}