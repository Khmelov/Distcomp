using Cassandra;
using Cassandra.Mapping;
using Discussion.Config;
using Discussion.Models;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Discussion.Repositories
{
    public class ReactionRepository : IReactionRepository
    {
        private readonly Cassandra.ISession _session;

        private readonly IMapper _mapper;

        public ReactionRepository(CassandraSettings settings)
        {
            var cluster = Cluster.Builder()
                .AddContactPoint(settings.ContactPoint)
                .WithPort(settings.Port)
                .Build();
            _session = cluster.Connect(settings.Keyspace);
            _mapper = new Mapper(_session);
        }

        public async Task<IEnumerable<Reaction>> GetAllAsync()
        {
            return await _mapper.FetchAsync<Reaction>("SELECT * FROM tbl_reaction");
        }

        public async Task<Reaction?> GetByIdAsync(long id)
        {
            return await _mapper.FirstOrDefaultAsync<Reaction>(
                "SELECT * FROM tbl_reaction WHERE id = ? ALLOW FILTERING", id);
        }

        public async Task CreateAsync(Reaction reaction)
        {
            await _mapper.InsertAsync(reaction);
        }

        public async Task UpdateAsync(long id, Reaction reaction)
        {
            string cql = @"UPDATE tbl_reaction 
                           SET content = ?, modified = ?
                           WHERE  topic_id = ? AND id = ?";
            await _mapper.ExecuteAsync(cql,
                reaction.Content,
                reaction.Modified,
                reaction.TopicId,
                id);
        }

        public async Task DeleteAsync(long id)
        {
            var reaction = await GetByIdAsync(id);
            if (reaction != null)
            {
                await _mapper.DeleteAsync<Reaction>("WHERE topic_id = ? AND id = ?",
                     reaction.TopicId, id);
            }
        }
    }
}
