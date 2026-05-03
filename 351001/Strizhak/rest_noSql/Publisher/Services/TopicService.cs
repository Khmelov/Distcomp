using AutoMapper;
using Publisher.Dtos;
using Publisher.Entities;
using Publisher.Repositories;
using System;
using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore;
namespace Publisher.Services
{
    public class TopicService : BaseService<Topic, TopicRequestTo, TopicResponseTo>
    {
        private readonly IRepository<Tag> _tagRepository;

        public TopicService(IRepository<Topic> repository, IRepository<Tag> tagRepository, IMapper mapper)
            : base(repository, mapper)
        {
            _tagRepository = tagRepository;
        }

        public override async Task<TopicResponseTo> CreateAsync(TopicRequestTo request)
        {
            // Проверка уникальности заголовка
            var existing = (await _repository.FindAsync(t => t.Title == request.Title)).FirstOrDefault();
            if (existing != null)
                throw new InvalidOperationException("Topic already exists");

            // Создаём сущность Topic
            var topic = _mapper.Map<Topic>(request);
            topic.Created = DateTime.UtcNow;
            topic.Modified = DateTime.UtcNow;

            // Обрабатываем теги, если они есть
            if (request.Tags != null && request.Tags.Any())
            {
                var distinctNames = request.Tags.Distinct().ToList();

                // Загружаем уже существующие теги из БД
                var existingTags = (await _tagRepository
                        .FindAsync(t => distinctNames.Contains(t.Name)))
                    .ToDictionary(t => t.Name);

                foreach (var name in distinctNames)
                {
                    Tag tag;
                    if (existingTags.TryGetValue(name, out var existingTag))
                    {
                        tag = existingTag; 
                    }
                    else
                    {
                        // создаём новый тег
                        tag = new Tag { Name = name };
                        await _tagRepository.AddAsync(tag); 
                    }

                    
                    topic.TopicTags.Add(new TopicTag { Tag = tag });
                }
            }

            // Сохраняем топик (все связанные сущности сохранятся автоматически)
            await _repository.AddAsync(topic);
            await _repository.SaveChangesAsync(); // единая транзакция

            return _mapper.Map<TopicResponseTo>(topic);
        }

        public override async Task<TopicResponseTo> UpdateAsync(long id, TopicRequestTo request)
        {
           
            var existingTopic = await _repository.GetByIdAsync(id);
            if (existingTopic == null)
                throw new KeyNotFoundException($"Topic with id {id} not found");
            if (existingTopic.Title != request.Title)
            {
                var existing = (await _repository.FindAsync(t => t.Title == request.Title)).FirstOrDefault();
                if (existing != null)
                    throw new InvalidOperationException("Topic with this title already exists");
            }
            _mapper.Map(request, existingTopic);
            existingTopic.Modified = DateTime.UtcNow; 

            _repository.Update(existingTopic);
            await _repository.SaveChangesAsync();

            return _mapper.Map<TopicResponseTo>(existingTopic);
        }
        public override async Task DeleteAsync(long id)
        {
            // Загружаем топик со всеми связями и тегами
            var topic = await _repository
                .Query()
                .Include(t => t.TopicTags)
                .ThenInclude(tt => tt.Tag)
                .FirstOrDefaultAsync(t => t.Id == id);

            if (topic == null)
                throw new KeyNotFoundException($"Topic with id {id} not found");

            // Сохраняем теги, которые были связаны с этим топиком
            var affectedTags = topic.TopicTags.Select(tt => tt.Tag).ToList();

            // Удаляем топик (связи удалятся автоматически благодаря Cascade)
            _repository.Delete(topic);
            await _repository.SaveChangesAsync();

            // Проверяем каждый тег: остались ли у него другие топики?
            foreach (var tag in affectedTags)
            {
                var tagWithTopics = await _tagRepository
                    .Query()
                    .Include(t => t.TopicTags)
                    .FirstOrDefaultAsync(t => t.Id == tag.Id);

                if (tagWithTopics != null && !tagWithTopics.TopicTags.Any())
                {
                    // Если связей больше нет, удаляем тег
                    _tagRepository.Delete(tagWithTopics);
                }
            }

            await _tagRepository.SaveChangesAsync(); // сохраняем удаление тегов
        }
    }
}