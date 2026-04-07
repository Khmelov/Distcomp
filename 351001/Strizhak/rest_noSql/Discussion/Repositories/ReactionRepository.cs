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
            await _mapper.InsertAsync(entity);
        }

        public async Task UpdateAsync(Reaction entity)
        {
            await _mapper.UpdateAsync(entity);
        }

        public async Task DeleteAsync(long topicId, long id)
        {
            await _mapper.DeleteAsync<Reaction>("WHERE topic_id = ? AND id = ?", topicId, id);
        }
        public async Task<Reaction?> GetByIdOnlyAsync(long id)
        {
            // В Cassandra WHERE без партиционного ключа требует ALLOW FILTERING
            string cql = "SELECT * FROM tbl_reaction WHERE id = ? ALLOW FILTERING";
            return await _mapper.FirstOrDefaultAsync<Reaction>(cql, id);
        }
    }
}