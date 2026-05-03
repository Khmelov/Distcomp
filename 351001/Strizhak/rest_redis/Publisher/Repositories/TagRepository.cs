using Microsoft.Extensions.Caching.Distributed;
using Publisher.Common;
using Publisher.Data;
using Publisher.Entities;

namespace Publisher.Repositories
{
    public class TagRepository : EfRepository<Tag>
    {
        public TagRepository(AppDbContext c, IDistributedCache ch) : base(c, ch) { }
        protected override void InvalidateCache(Tag? entity, string? id, string listKey)
        {
            base.InvalidateCache(entity, id, listKey);
            if (id != null) _cache.RemoveAsync(CacheKeys.TopicsByTag(id));
        }
    }
}
