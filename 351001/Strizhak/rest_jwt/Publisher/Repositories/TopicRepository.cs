using Microsoft.Extensions.Caching.Distributed;
using Publisher.Common;
using Publisher.Data;
using Publisher.Entities;

namespace Publisher.Repositories
{
    public class TopicRepository : EfRepository<Topic>
    {
        public TopicRepository(AppDbContext c, IDistributedCache ch) : base(c, ch) { }
        protected override void InvalidateCache(Topic? entity, string? id, string listKey)
        {
            base.InvalidateCache(entity, id, listKey);
            _cache.RemoveAsync(CacheKeys.SearchResults());
            if (id != null)
            {
                _cache.RemoveAsync(CacheKeys.ReactionsByTopic(id));
                _cache.RemoveAsync(CacheKeys.TagsByTopic(id));
            }
        }
    }
}
