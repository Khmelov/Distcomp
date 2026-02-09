
using AutoMapper;
using Task310RestApi.DTOs.Request;
using Task310RestApi.DTOs.Response;
using Task310RestApi.Exceptions;
using Task310RestApi.Interfaces;
using Task310RestApi.Models;
using Task310RestApi.Repositories;

namespace Task310RestApi.Services
{
    public class NewsService : INewsService
    {
        private readonly IRepository<News> _newsRepository;
        private readonly IRepository<Creator> _creatorRepository;
        private readonly IRepository<Label> _labelRepository;
        private readonly IMapper _mapper;

        public NewsService(
            IRepository<News> newsRepository,
            IRepository<Creator> creatorRepository,
            IRepository<Label> labelRepository,
            IMapper mapper)
        {
            _newsRepository = newsRepository;
            _creatorRepository = creatorRepository;
            _labelRepository = labelRepository;
            _mapper = mapper;
        }

        public async Task<IEnumerable<NewsResponseTo>> GetAllNewsAsync()
        {
            var news = await _newsRepository.GetAllAsync();
            return _mapper.Map<IEnumerable<NewsResponseTo>>(news);
        }

        public async Task<NewsResponseTo?> GetNewsByIdAsync(long id)
        {
            var news = await _newsRepository.GetByIdAsync(id);
            if (news == null)
            {
                throw new ResourceNotFoundException($"News not found with id: {id}");
            }
            return _mapper.Map<NewsResponseTo>(news);
        }

        public async Task<NewsResponseTo> CreateNewsAsync(NewsRequestTo newsRequest)
        {
            ValidateNewsRequest(newsRequest);
            
            // Проверяем существование создателя
            if (!await _creatorRepository.ExistsAsync(newsRequest.CreatorId))
            {
                throw new ValidationException($"Creator not found with id: {newsRequest.CreatorId}", "40002");
            }

            // Проверяем существование меток
            if (newsRequest.LabelIds != null && newsRequest.LabelIds.Any())
            {
                foreach (var labelId in newsRequest.LabelIds)
                {
                    if (!await _labelRepository.ExistsAsync(labelId))
                    {
                        throw new ValidationException($"Label not found with id: {labelId}", "40003");
                    }
                }
            }

            var news = _mapper.Map<News>(newsRequest);
            news.Created = DateTime.UtcNow;
            news.Modified = DateTime.UtcNow;
            
            var createdNews = await _newsRepository.CreateAsync(news);
            return _mapper.Map<NewsResponseTo>(createdNews);
        }

        public async Task<NewsResponseTo?> UpdateNewsAsync(long id, NewsRequestTo newsRequest)
        {
            ValidateNewsRequest(newsRequest);
            
            var existingNews = await _newsRepository.GetByIdAsync(id);
            if (existingNews == null)
            {
                throw new ResourceNotFoundException($"News not found with id: {id}");
            }

            // Проверяем существование создателя
            if (!await _creatorRepository.ExistsAsync(newsRequest.CreatorId))
            {
                throw new ValidationException($"Creator not found with id: {newsRequest.CreatorId}", "40002");
            }

            // Проверяем существование меток
            if (newsRequest.LabelIds != null && newsRequest.LabelIds.Any())
            {
                foreach (var labelId in newsRequest.LabelIds)
                {
                    if (!await _labelRepository.ExistsAsync(labelId))
                    {
                        throw new ValidationException($"Label not found with id: {labelId}", "40003");
                    }
                }
            }

            _mapper.Map(newsRequest, existingNews);
            existingNews.Modified = DateTime.UtcNow;
            
            var updatedNews = await _newsRepository.UpdateAsync(existingNews);
            return _mapper.Map<NewsResponseTo>(updatedNews);
        }

        public async Task<bool> DeleteNewsAsync(long id)
        {
            if (!await _newsRepository.ExistsAsync(id))
            {
                throw new ResourceNotFoundException($"News not found with id: {id}");
            }

            return await _newsRepository.DeleteAsync(id);
        }

        public async Task<bool> ExistsAsync(long id)
        {
            return await _newsRepository.ExistsAsync(id);
        }

        public async Task<IEnumerable<NewsResponseTo>> GetNewsByParamsAsync(
            List<string>? labelNames,
            List<long>? labelIds,
            string? creatorLogin,
            string? title,
            string? content)
        {
            var allNews = await _newsRepository.GetAllAsync();
            var filteredNews = allNews.AsEnumerable();

            // Фильтрация по названиям меток
            if (labelNames != null && labelNames.Any())
            {
                var labelRepo = _labelRepository as InMemoryLabelRepository;
                if (labelRepo != null)
                {
                    var labels = await labelRepo.GetByNamesAsync(labelNames);
                    var labelIdsFromNames = labels.Select(l => l.Id).ToList();
                    
                    if (labelIdsFromNames.Any())
                    {
                        filteredNews = filteredNews.Where(n => n.LabelIds.Any(id => labelIdsFromNames.Contains(id)));
                    }
                }
            }

            // Фильтрация по ID меток
            if (labelIds != null && labelIds.Any())
            {
                filteredNews = filteredNews.Where(n => n.LabelIds.Any(id => labelIds.Contains(id)));
            }

            // Фильтрация по логину создателя
            if (!string.IsNullOrEmpty(creatorLogin))
            {
                var creatorRepo = _creatorRepository as InMemoryCreatorRepository;
                if (creatorRepo != null)
                {
                    var creator = await creatorRepo.FindByLoginAsync(creatorLogin);
                    if (creator != null)
                    {
                        filteredNews = filteredNews.Where(n => n.CreatorId == creator.Id);
                    }
                    else
                    {
                        return new List<NewsResponseTo>();
                    }
                }
            }

            // Фильтрация по заголовку
            if (!string.IsNullOrEmpty(title))
            {
                filteredNews = filteredNews.Where(n => 
                    n.Title.Contains(title, StringComparison.OrdinalIgnoreCase));
            }

            // Фильтрация по содержимому
            if (!string.IsNullOrEmpty(content))
            {
                filteredNews = filteredNews.Where(n => 
                    n.Content.Contains(content, StringComparison.OrdinalIgnoreCase));
            }

            return _mapper.Map<IEnumerable<NewsResponseTo>>(filteredNews);
        }

        private void ValidateNewsRequest(NewsRequestTo request)
        {
            var validationResults = new List<System.ComponentModel.DataAnnotations.ValidationResult>();
            var validationContext = new System.ComponentModel.DataAnnotations.ValidationContext(request);
            
            if (!System.ComponentModel.DataAnnotations.Validator.TryValidateObject(request, validationContext, validationResults, true))
            {
                var errorMessages = string.Join("; ", validationResults.Select(r => r.ErrorMessage));
                throw new ValidationException(errorMessages, "40000");
            }
        }
    }
}
