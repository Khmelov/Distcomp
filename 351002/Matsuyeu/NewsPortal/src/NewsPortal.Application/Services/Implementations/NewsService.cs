using NewsPortal.Models.Dtos.RequestTo;
using NewsPortal.Models.Entities;
using NewsPortal.Models.Exceptions;
using NewsPortal.Models.Repositories.Abstractions;

namespace NewsPortal.Services.Abstractions
{

    public class NewsService : INewsService
    {
        private readonly IRepository<News> _newsRepository;
        private readonly IRepository<Creator> _creatorRepository;
        private readonly IRepository<Mark> _markRepository;

        public NewsService(
            IRepository<News> newsRepository,
            IRepository<Creator> creatorRepository,
            IRepository<Mark> markRepository)
        {
            _newsRepository = newsRepository;
            _creatorRepository = creatorRepository;
            _markRepository = markRepository;
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

            // Возвращаем полный ответ с названиями меток
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

            // Проверка наличия связанных заметок
            if (existingNews.Notes != null && existingNews.Notes.Any())
                throw new ConflictException("Cannot delete news that has notes");

            // Сохраняем список меток до удаления новости
            var marksToCheck = existingNews.Marks?.ToList() ?? new List<Mark>();

            // Удаляем новость
            await _newsRepository.DeleteAsync(id);

            // Проверяем каждую метку - остались ли у нее другие новости
            foreach (var mark in marksToCheck)
            {
                // Получаем обновленную метку из БД (после удаления новости)
                var updatedMark = await _markRepository.GetByIdAsync(mark.Id);

                // Если метка существует и у нее больше нет связанных новостей
                if (updatedMark != null && (updatedMark.News == null || !updatedMark.News.Any()))
                {
                    // Дополнительная проверка через репозиторий, если коллекция не загружена
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

            // Находим метку по имени
            var mark = await _markRepository.FindSingleAsync(m => m.Name == markName.Trim());
            if (mark == null)
                return new List<NewsResponseTo>();

            // Получаем все новости и фильтруем по метке
            // В реальном проекте лучше сделать специализированный запрос в репозитории
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
            var processedNames = new HashSet<string>(); // Для избежания дубликатов

            foreach (var markName in markNames.Select(n => n.Trim()).Where(n => !string.IsNullOrWhiteSpace(n)))
            {
                // Пропускаем дубликаты в запросе
                if (processedNames.Contains(markName))
                    continue;

                processedNames.Add(markName);

                // Валидация имени метки
                if (markName.Length < 2 || markName.Length > 32)
                    throw new BadRequestException($"Mark name '{markName}' must be between 2 and 32 characters");

                // Ищем существующую метку по имени
                var existingMark = await _markRepository.FindSingleAsync(m => m.Name == markName);

                if (existingMark != null)
                {
                    // Если метка существует, добавляем её
                    marks.Add(existingMark);
                }
                else
                {
                    // Если метка не существует, создаем новую
                    var newMark = new Mark
                    {
                        Name = markName
                    };

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
                Marks = news.Marks?.Select(m => m.Name).ToList() ?? new List<string>()
            };
        }

        #endregion
    }
}