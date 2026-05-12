using Cassandra;
using Cassandra.Mapping;
using Discussion.Entities;

namespace Discussion.Repositories
{
    

    public class ReactionRepository : IReactionRepository
    {
        private readonly IMapper _mapper;

        public ReactionRepository(IMapper mapper)
        {
            _mapper = mapper;
        }

        public async Task<Reaction?> GetByIdAsync(long topicId, long id)
        {
            return await _mapper.FirstOrDefaultAsync<Reaction>(
                "WHERE topic_id = ? AND id = ?", topicId, id);
        }

        public async Task<IEnumerable<Reaction>> GetByTopicIdAsync(long topicId)
        {
            return await _mapper.FetchAsync<Reaction>("WHERE topic_id = ?", topicId);
        }

        public async Task AddAsync(Reaction entity)
        {
          //  Console.WriteLine($"Saving reaction with id {entity.Id}");
            await _mapper.InsertAsync(entity);
        }

        public async Task UpdateAsync(Reaction entity)
        {
            await _mapper.UpdateAsync(entity);
        }

        // Вспомогательный класс (можно разместить внутри класса репозитория)
        private class TopicIdHolder
        {
            public long topic_id { get; set; }
        }

        public async Task DeleteAsync(long id)
        {
            var findCql = "SELECT topic_id FROM tbl_reaction WHERE id = ? ALLOW FILTERING";
            var result = await _mapper.FirstOrDefaultAsync<TopicIdHolder>(findCql, id);
            if (result == null)
                throw new KeyNotFoundException($"Reaction with id {id} not found");

            var deleteCql = "DELETE FROM tbl_reaction WHERE topic_id = ? AND id = ?";
            await _mapper.ExecuteAsync(deleteCql, result.topic_id, id);
        }
        public async Task<Reaction?> GetByIdOnlyAsync(long id)
        {
            // В Cassandra WHERE без партиционного ключа требует ALLOW FILTERING
            string cql = "SELECT * FROM tbl_reaction WHERE id = ? ALLOW FILTERING";

            return await _mapper.FirstOrDefaultAsync<Reaction>(cql, id);
        }
        public async Task<IEnumerable<Reaction>> GetAllAsync()
        {
            // ВНИМАНИЕ: ALLOW FILTERING неэффективно, но для тестов допустимо
            var cql = "SELECT * FROM tbl_reaction ALLOW FILTERING";
            return await _mapper.FetchAsync<Reaction>(cql);
        }
    }
}