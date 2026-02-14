using Application.DTOs.Requests;
using Application.DTOs.Responses;
using Application.Exceptions;
using Application.Interfaces;
using AutoMapper;
using Core.Entities;

namespace Application.Services
{
    public class NewsService : INewsService
    {
        private readonly IMapper _mapper;

        private readonly INewsRepository _newsRepository;

        public NewsService(IMapper mapper, INewsRepository repository)
        {
            _mapper = mapper;
            _newsRepository = repository;
        }

        public async Task<NewsResponseTo> CreateNews(NewsRequestTo createNewsRequestTo)
        {
            News newsFromDto = _mapper.Map<News>(createNewsRequestTo);

            News createdNews = await _newsRepository.AddAsync(newsFromDto);

            NewsResponseTo dtoFromCreatedNews = _mapper.Map<NewsResponseTo>(createdNews);

            return dtoFromCreatedNews;
        }

        public async Task DeleteNews(NewsRequestTo deleteNewsRequestTo)
        {
            News newsFromDto = _mapper.Map<News>(deleteNewsRequestTo);
            _ = await _newsRepository.DeleteAsync(newsFromDto) ?? throw new NewNotFoundException($"Deleted news {newsFromDto} was not found");
        }

        public async Task<IEnumerable<NewsResponseTo>> GetAllNews()
        {
            IEnumerable<News> allNews = await _newsRepository.GetAllAsync();
            
            var allNewsResponseTos = new List<NewsResponseTo>();
            foreach (News news in allNews)
            {
                NewsResponseTo newsTo = _mapper.Map<NewsResponseTo>(news);
                allNewsResponseTos.Add(newsTo);
            }

            return allNewsResponseTos;
        }

        public async Task<NewsResponseTo> GetNews(NewsRequestTo getNewsRequestTo)
        {
            News newsFromDto = _mapper.Map<News>(getNewsRequestTo);

            News demandedNews = await _newsRepository.GetByIdAsync(newsFromDto.Id) ?? throw new NewNotFoundException($"Demanded news {newsFromDto} was not found");

            NewsResponseTo demandedNewsResponseTo = _mapper.Map<NewsResponseTo>(demandedNews);

            return demandedNewsResponseTo;
        }

        public async Task<NewsResponseTo> UpdateNews(NewsRequestTo updateNewsRequestTo)
        {
            News newsFromDto = _mapper.Map<News>(updateNewsRequestTo);

            News? updateNews = await _newsRepository.UpdateAsync(newsFromDto) ?? throw new NewNotFoundException($"Update news {newsFromDto} was not found");

            NewsResponseTo updateNewsResponseTo = _mapper.Map<NewsResponseTo>(updateNews);

            return updateNewsResponseTo;
        }
    }
}
