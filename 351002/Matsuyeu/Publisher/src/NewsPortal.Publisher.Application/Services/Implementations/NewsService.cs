using Publisher.src.NewsPortal.Publisher.Application.Dtos.RequestTo;
using Publisher.src.NewsPortal.Publisher.Application.Dtos.ResponseTo;
using Publisher.src.NewsPortal.Publisher.Application.Services.Abstractions;
using Publisher.src.NewsPortal.Publisher.Domain.Entities;
using Publisher.src.NewsPortal.Publisher.Domain.Exceptions;
using Publisher.src.NewsPortal.Publisher.Infrastructure.Clients.Abstractions;
using Publisher.src.NewsPortal.Publisher.Infrastructure.Repositories.Abstractions;

namespace Publisher.src.NewsPortal.Publisher.Application.Services.Implementations
{

    public class NewsService : INewsService
    {
        private readonly IRepository<News> _newsRepository;
        private readonly IRepository<Creator> _creatorRepository;
        private readonly IRepository<Mark> _markRepository;
        private readonly IDiscussionApiClient _discussionApiClient;

        public NewsService(
            IRepository<News> newsRepository,
            IRepository<Creator> creatorRepository,
            IRepository<Mark> markRepository,
            IDiscussionApiClient discussionApiClient)
        {
            _newsRepository = newsRepository;
            _creatorRepository = creatorRepository;
            _markRepository = markRepository;
            _discussionApiClient = discussionApiClient;
        }

        public async Task<IEnumerable<NewsResponseTo>> GetAllNewsAsync()
        {
            var newsList = await _newsRepository.GetAllAsync();
            var responseList = new List<NewsResponseTo>();

            foreach (var news in newsList)
            {
                responseList.Add(await BuildResponseAsync(news));
            }

            return responseList;
        }

        public async Task<NewsResponseTo?> GetNewsByIdAsync(long id)
        {
            if (id <= 0)
                throw new BadRequestException("ID must be greater than 0");

            var news = await _newsRepository.GetByIdAsync(id);
            if (news == null)
                throw new NotFoundException($"News with ID {id} not found");

            return await BuildResponseAsync(news);
        }

        public async Task<NewsResponseTo> CreateNewsAsync(NewsRequestTo newsRequest)
        {
            // Проверка существования Creator
            await ValidateCreatorExistsAsync(newsRequest.CreatorId);

            // Проверка уникальности заголовка
            var existingNews = await _newsRepository.FindSingleAsync(n => n.Title == newsRequest.Title);
            if (existingNews != null)
                throw new ConflictException($"News with title '{newsRequest.Title}' already exists");

            var news = new News
            {
                CreatorId = newsRequest.CreatorId,
                Title = newsRequest.Title.Trim(),
                Content = newsRequest.Content.Trim(),
                Created = DateTime.UtcNow,
                Modified = DateTime.UtcNow,
                Marks = new List<Mark>()
            };

            // Обработка меток по именам (создаем новые, если не существуют)
            if (newsRequest.Marks != null && newsRequest.Marks.Any())
            {
                news.Marks = await ProcessMarksAsync(newsRequest.Marks);
            }

            // Сохраняем новость с метками
            var createdNews = await _newsRepository.AddAsync(news);

            return await BuildResponseAsync(createdNews);
        }

        public async Task<bool> UpdateNewsAsync(NewsRequestTo newsRequest)
        {
            if (newsRequest.Id <= 0)
                throw new BadRequestException("ID must be greater than 0");

            var existingNews = await _newsRepository.GetByIdAsync(newsRequest.Id);
            if (existingNews == null)
                throw new NotFoundException($"News with ID {newsRequest.Id} not found");

            // Проверка существования Creator, если ID изменился
            if (existingNews.CreatorId != newsRequest.CreatorId)
            {
                await ValidateCreatorExistsAsync(newsRequest.CreatorId);
            }

            // Проверка уникальности заголовка (исключая текущую новость)
            var duplicateNews = await _newsRepository.FindSingleAsync(n =>
                n.Title == newsRequest.Title && n.Id != newsRequest.Id);
            if (duplicateNews != null)
                throw new ConflictException($"News with title '{newsRequest.Title}' already exists");

            // Обновление полей
            existingNews.CreatorId = newsRequest.CreatorId;
            existingNews.Title = newsRequest.Title.Trim();
            existingNews.Content = newsRequest.Content.Trim();
            existingNews.Modified = DateTime.UtcNow;

            // Обновление меток
            if (newsRequest.Marks != null)
            {
                existingNews.Marks = await ProcessMarksAsync(newsRequest.Marks);
            }

            await _newsRepository.UpdateAsync(existingNews);
            return true;
        }

        public async Task<bool> DeleteNewsAsync(long id)
        {
            if (id <= 0)
                throw new BadRequestException("ID must be greater than 0");

            var existingNews = await _newsRepository.GetByIdAsync(id);
            if (existingNews == null)
                throw new NotFoundException($"News with ID {id} not found");

            // Удаляем связанные заметки через Discussion API (каскадное удаление)
            try
            {
                await _discussionApiClient.DeleteNotesByNewsIdAsync(id);
                Console.WriteLine($"Deleted all notes for news {id}");
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Warning: Failed to delete notes for news {id}: {ex.Message}");
                // Продолжаем удаление новости даже если не получилось удалить заметки
            }

            // Сохраняем список меток до удаления новости
            var marksToCheck = existingNews.Marks?.ToList() ?? new List<Mark>();

            // Удаляем новость
            await _newsRepository.DeleteAsync(id);

            // Проверяем каждую метку - остались ли у нее другие новости
            foreach (var mark in marksToCheck)
            {
                var updatedMark = await _markRepository.GetByIdAsync(mark.Id);

                if (updatedMark != null && (updatedMark.News == null || !updatedMark.News.Any()))
                {
                    var relatedNews = await _newsRepository.FindAsync(n => n.Marks.Any(m => m.Id == mark.Id));
                    if (!relatedNews.Any())
                    {
                        await _markRepository.DeleteAsync(mark.Id);
                    }
                }
            }

            return true;
        }

        public async Task<PagedResult<NewsResponseTo>> GetPagedNewsAsync(QueryParameters parameters)
        {
            var pagedResult = await _newsRepository.GetPagedAsync(parameters);

            var items = new List<NewsResponseTo>();
            foreach (var news in pagedResult.Items)
            {
                items.Add(await BuildResponseAsync(news));
            }

            return new PagedResult<NewsResponseTo>
            {
                Items = items,
                TotalCount = pagedResult.TotalCount,
                PageNumber = pagedResult.PageNumber,
                PageSize = pagedResult.PageSize
            };
        }

        public async Task<IEnumerable<NewsResponseTo>> GetNewsByCreatorIdAsync(long creatorId)
        {
            if (creatorId <= 0)
                throw new BadRequestException("CreatorId must be greater than 0");

            await ValidateCreatorExistsAsync(creatorId);

            var newsList = await _newsRepository.FindAsync(n => n.CreatorId == creatorId);
            var responseList = new List<NewsResponseTo>();

            foreach (var news in newsList)
            {
                responseList.Add(await BuildResponseAsync(news));
            }

            return responseList;
        }

        public async Task<IEnumerable<NewsResponseTo>> GetNewsByMarkNameAsync(string markName)
        {
            if (string.IsNullOrWhiteSpace(markName))
                throw new BadRequestException("Mark name cannot be empty");

            var mark = await _markRepository.FindSingleAsync(m => m.Name == markName.Trim());
            if (mark == null)
                return new List<NewsResponseTo>();

            var allNews = await _newsRepository.GetAllAsync();
            var newsWithMark = allNews.Where(n => n.Marks != null && n.Marks.Any(m => m.Id == mark.Id));

            var responseList = new List<NewsResponseTo>();
            foreach (var news in newsWithMark)
            {
                responseList.Add(await BuildResponseAsync(news));
            }

            return responseList;
        }

        #region Private Methods

        /// <summary>
        /// Обрабатывает список названий меток: находит существующие или создает новые
        /// </summary>
        private async Task<List<Mark>> ProcessMarksAsync(List<string> markNames)
        {
            var marks = new List<Mark>();
            var processedNames = new HashSet<string>();

            foreach (var markName in markNames.Select(n => n.Trim()).Where(n => !string.IsNullOrWhiteSpace(n)))
            {
                if (processedNames.Contains(markName))
                    continue;

                processedNames.Add(markName);

                if (markName.Length < 2 || markName.Length > 32)
                    throw new BadRequestException($"Mark name '{markName}' must be between 2 and 32 characters");

                var existingMark = await _markRepository.FindSingleAsync(m => m.Name == markName);

                if (existingMark != null)
                {
                    marks.Add(existingMark);
                }
                else
                {
                    var newMark = new Mark { Name = markName };
                    var createdMark = await _markRepository.AddAsync(newMark);
                    marks.Add(createdMark);
                }
            }

            return marks;
        }

        private async Task ValidateCreatorExistsAsync(long creatorId)
        {
            var creator = await _creatorRepository.GetByIdAsync(creatorId);
            if (creator == null)
                throw new NotFoundException($"Creator with ID {creatorId} does not exist");
        }

        private async Task<NewsResponseTo> BuildResponseAsync(News news)
        {
            var creator = await _creatorRepository.GetByIdAsync(news.CreatorId);

            return new NewsResponseTo
            {
                Id = news.Id,
                CreatorId = news.CreatorId,
                CreatorLogin = creator?.Login ?? string.Empty,
                Title = news.Title,
                Content = news.Content,
                Created = news.Created,
                Modified = news.Modified,
                Marks = news.Marks?.Select(m => m.Name).ToList() ?? new List<string>(),
            };
        }

        #endregion
    }
}