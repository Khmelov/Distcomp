using System.Linq.Expressions;
using Cassandra;
using Cassandra.Mapping;
using Shared.Domain.Interfaces;

namespace DiscussionService.Infrastructure.Cassandra.Repositories;

public class CassandraCommentRepository<Id, Entity> : IRepository<Id, Entity> where Entity : class, IEntity<Id>
{
    private readonly IMapper _mapper;

    public CassandraCommentRepository(ISession session)
    {
        // Подключаем маппинг, который ты создал
        var config = new MappingConfiguration().Define<CassandraMapping<Id>>();
        _mapper = new Mapper(session, config);
    }
    
    /// <remarks>
    /// В Cassandra важно, чтобы id был частью Primary Key.
    /// Маппер сам подставит имя колонки из твоих настроек CassandraMapping
    /// </remarks>>
    public async Task<Entity?> GetByIdAsync(Id id)
    {
        return await _mapper.SingleOrDefaultAsync<Entity>("WHERE id = ? ALLOW FILTERING", id);
    }

    /// <remarks>
    /// ОПАСНО: В реальных проектах для Cassandra это делается через FetchAsync 
    /// с ограничением LIMIT или по конкретному ключу.
    /// </remarks>>
    public async Task<IEnumerable<Entity>> GetAllAsync()
    {
        return await _mapper.FetchAsync<Entity>(); 
    }

    // public async Task AddAsync(Entity entity)
    // {
    //     long newId = DateTime.UtcNow.Ticks;
    //
    //     ((dynamic)entity).ID = (dynamic)newId;
    //     
    //     await _mapper.InsertAsync(entity);
    // }
    
    public async Task AddAsync(Entity entity)
    {
        await _mapper.InsertAsync(entity);
    }

    public async Task UpdateAsync(Entity entity)
    {
        await _mapper.UpdateAsync(entity);
    }
    /// <remarks>
    /// Удаление происходит по первичному ключу
    /// </remarks>>
    public async Task DeleteAsync(Id id)
    {
        var existing = (await _mapper.FetchAsync<Entity>("WHERE id = ? ALLOW FILTERING", id)).FirstOrDefault();
    
        if (existing == null)
        {
            throw new KeyNotFoundException("Comment not found"); 
        }

        await _mapper.DeleteAsync(existing);
    }

    public Task<bool> ExistsAsync(Expression<Func<Entity, bool>> condition)
    {
        throw new NotImplementedException();
    }

    /// <remarks>
    /// Благодаря тому, что StoryID — это PartitionKey, этот запрос будет выполняться мгновенно
    /// </remarks>
    public async Task<IEnumerable<Entity>> GetByStoryIdAsync(Id storyId)
    {
        return await _mapper.FetchAsync<Entity>("WHERE story_id = ?", storyId);
    }
}