using AutoMapper;
using BusinessLogic.DTO.Request;
using BusinessLogic.DTO.Response;
using BusinessLogic.Servicies;
using Cassandra;
using DataAccess.Models;

namespace Infrastructure.ServiceImplementation
{
    public class PostService : IBaseService<PostRequestTo, PostResponseTo>
    {
        private readonly Cassandra.Mapping.IMapper _cassandraMapper;
        private readonly IMapper _objectMapper;
        private const int BUCKETS_COUNT = 10;

        public PostService(ISession session, IMapper objectMapper)
        {
            _cassandraMapper = new Cassandra.Mapping.Mapper(session);
            _objectMapper = objectMapper;
        }

        private int GetBucket(int storyId) => Math.Abs(storyId % BUCKETS_COUNT);

        public virtual async Task<PostResponseTo> CreateAsync(PostRequestTo entity)
        {
            // 1. Генерируем ID
            int generatedId = (int)DateTimeOffset.UtcNow.ToUnixTimeSeconds();

            // 2. Маппим DTO в объекты БД (используем _objectMapper)
            var postById = _objectMapper.Map<PostById>(entity);
            var postByStory = _objectMapper.Map<PostByStory>(entity);

            // 3. Устанавливаем системные поля вручную
            postById.Id = generatedId;
            postByStory.Id = generatedId;
            postByStory.Bucket = GetBucket(entity.StoryId);

            // 4. Сохраняем в обе таблицы
            await _cassandraMapper.InsertAsync(postById);
            await _cassandraMapper.InsertAsync(postByStory);

            // 5. Формируем ответ
            var response = _objectMapper.Map<PostResponseTo>(entity);
            response.Id = generatedId;

            return response;
        }

        public async Task<PostResponseTo?> GetByIdAsync(int id)
        {
            // Читаем из таблицы, оптимизированной для поиска по ID
            var post = await _cassandraMapper.FirstOrDefaultAsync<PostById>("WHERE post_id = ?", id);
            return post != null ? _objectMapper.Map<PostResponseTo>(post) : null;
        }

        public async Task<bool> DeleteByIdAsync(int id)
        {
            // Нам нужно знать story_id, чтобы удалить запись из второй таблицы, 
            // поэтому сначала находим запись в posts_by_id
            var existing = await _cassandraMapper.FirstOrDefaultAsync<PostById>("WHERE post_id = ?", id);
            if (existing == null) return false;

            var bucket = GetBucket(existing.StoryId);

            // Удаляем из обеих таблиц
            await _cassandraMapper.DeleteAsync<PostById>("WHERE post_id = ?", id);
            await _cassandraMapper.DeleteAsync<PostByStory>("WHERE story_id = ? AND bucket = ? AND post_id = ?",
                existing.StoryId, bucket, id);

            return true;
        }

        public async Task<PostResponseTo?> UpdateAsync(PostRequestTo entity)
        {
            // Находим старую версию, чтобы проверить, не изменился ли story_id
            var existing = await _cassandraMapper.FirstOrDefaultAsync<PostById>("WHERE post_id = ?", entity.Id);
            if (existing == null) return null;

            var bucket = GetBucket(entity.StoryId);

            // Если story_id изменился, старая запись в posts_by_story станет "сиротой". Ее нужно удалить.
            if (existing.StoryId != entity.StoryId)
            {
                await _cassandraMapper.DeleteAsync<PostByStory>("WHERE story_id = ? AND bucket = ? AND post_id = ?",
                    existing.StoryId, GetBucket(existing.StoryId), entity.Id);
            }

            // Маппим обновленные данные
            var postById = _objectMapper.Map<PostById>(entity);
            var postByStory = _objectMapper.Map<PostByStory>(entity);

            postByStory.Bucket = bucket; // Не забываем про бакет

            // Обновляем обе таблицы
            await _cassandraMapper.UpdateAsync(postById);
            await _cassandraMapper.UpdateAsync(postByStory);

            return _objectMapper.Map<PostResponseTo>(postById);
        }

        public async Task<List<PostResponseTo>> GetAllAsync()
        {
            // Сканирование всей таблицы (в Cassandra это медленно, но для GetAll подходит)
            var posts = await _cassandraMapper.FetchAsync<PostById>("");
            return _objectMapper.Map<List<PostResponseTo>>(posts);
        }
    }
}