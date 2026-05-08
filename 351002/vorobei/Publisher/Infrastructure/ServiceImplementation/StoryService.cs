using AutoMapper;
using BusinessLogic.DTO.Request;
using BusinessLogic.DTO.Response;
using BusinessLogic.Repositories;
using DataAccess.Models;
using Infrastructure.DatabaseContext;
using Infrastructure.Exceptions;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Caching.Distributed;

namespace Infrastructure.ServiceImplementation
{
    public class StoryService : BaseService<Story, StoryRequestTo, StoryResponseTo>
    {
        private readonly IRepository<Creator> _creatorRepository;
        private readonly IRepository<Mark> _markRepository;

        public StoryService(
            IRepository<Story> repository,
            IRepository<Creator> creatorRepository,
            IRepository<Mark> markRepository,
            IMapper mapper,
            IDistributedCache cache)
            : base(repository, mapper, cache)
        {
            _creatorRepository = creatorRepository;
            _markRepository = markRepository;
        }

        public async override Task<StoryResponseTo> CreateAsync(StoryRequestTo entity)
        {
            // 1. Валидация автора
            if (!await _creatorRepository.ExistsAsync(entity.CreatorId))
            {
                throw new BaseException(403, "Creator with such id does not exists");
            }

            // 2. Валидация уникальности (Title + CreatorId)
            var allStories = await _repository.GetAllAsync();
            if (allStories.Any(c => c.Title == entity.Title && c.CreatorId == entity.CreatorId))
            {
                throw new BaseException(403, "Story with such title and creatorId already exists");
            }

            // 3. Маппинг и подготовка объекта
            Story story = _mapper.Map<Story>(entity);
            story.Id = await _repository.GetLastIdAsync() + 1;
            story.Marks = new List<Mark>();

            // 4. Обработка меток (Marks)
            var allExistingMarks = await _markRepository.GetAllAsync();
            foreach (var markName in entity.Marks)
            {
                var mark = allExistingMarks.FirstOrDefault(m => m.Name == markName);
                if (mark == null)
                {
                    mark = new Mark
                    {
                        Name = markName,
                        Id = await _markRepository.GetLastIdAsync() + 1
                    };
                    await _markRepository.CreateAsync(mark);
                    // Обновляем локальный список, чтобы не создавать дубликаты в рамках одного цикла
                    allExistingMarks.Add(mark);
                }
                story.Marks.Add(mark);
            }

            // 5. Сохранение в БД
            await _repository.CreateAsync(story);

            // 6. РАБОТА С КЭШЕМ (Логика возвращена и исправлена)
            var response = _mapper.Map<StoryResponseTo>(story);

            // Сохраняем индивидуальную запись в кэш
            await SetCacheAsync(GetCacheKey(story.Id), response);

            // Инвалидируем список "GetAll", так как добавилась новая история
            await InvalidateAllCacheAsync();

            return response;
        }
    }
}
