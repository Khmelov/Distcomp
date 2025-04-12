using Cassandra;
using Cassandra.Mapping;
using Discussion.Data.Mapping;
using Discussion.Models;
using ISession = Cassandra.ISession;

namespace Discussion.Data.Implementation {
    public class CommentRepository : ICommentRepository, IDisposable {

        public const string DefaultCountry = "by";

        private ISession session;
        private IMapper mapper;

        public CommentRepository(Cluster cluster) {
            session = cluster.Connect("distcomp");
            mapper = new Mapper(
                session,
                new MappingConfiguration().Define<CommentMappings>()
            );
        }

        public async Task<IEnumerable<Comment>> GetAllAsync() {
            return (await mapper.FetchAsync<Comment>()).ToList();
        }

        public async Task<Comment?> GetAsync(long id) {
            return await mapper.FirstOrDefaultAsync<Comment>(
                "WHERE country = ? AND id = ?", DefaultCountry, id);
        }

        public async Task<Comment> CreateAsync(Comment entity) {
            entity.Country = DefaultCountry;
            entity.Id = BitConverter.ToInt64(Guid.NewGuid().ToByteArray());
            await mapper.InsertAsync(entity);
            return entity;
        }

        public async Task<Comment> UpdateAsync(Comment entity) {
            await mapper.UpdateAsync(entity);
            return entity;
        }

        public async Task DeleteAsync(long id) {
            var entity = await mapper.FirstOrDefaultAsync<Comment>(
                "WHERE country = ? AND id = ?", DefaultCountry, id);
            if (entity is null) {
                throw new KeyNotFoundException("Entity with specified ID does not exist");
            }
            await mapper.DeleteAsync(entity);
        }

        public void Dispose() {
            session.ShutdownAsync().Wait();
        }
    }
}
