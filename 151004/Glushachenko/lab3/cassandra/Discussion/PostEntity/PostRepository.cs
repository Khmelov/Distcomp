using Cassandra.Mapping;
using Discussion.Common;
using Discussion.PostEntity.Interface;
using Discussion.Storage.Cassandra;

namespace Discussion.PostEntity
{
    public class PostRepository(CassandraStorage storage, Random random) : AbstractCrudRepository<Post>(storage, random), IPostRepository
    {
        private readonly IMapper _mapper = storage.Mapper;

        public override Post Add(Post entity)
        {
            entity.Country = "Belarus";

            return base.Add(entity);
        }

        public override async Task<Post> AddAsync(Post entity)
        {
            entity.Country = "Belarus";

            return await base.AddAsync(entity);
        }

        public override async Task<Post> UpdateAsync(Post entity)
        {
            entity.Country = "Belarus";

            return await base.UpdateAsync(entity);
        }
    }
}
