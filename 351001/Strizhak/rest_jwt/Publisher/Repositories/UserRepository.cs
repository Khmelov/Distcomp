// Для Пользователя
using Microsoft.Extensions.Caching.Distributed;
using Publisher.Common;
using Publisher.Data;
using Publisher.Entities;
using Publisher.Repositories;

public class UserRepository : EfRepository<User>
{
    public UserRepository(AppDbContext c, IDistributedCache ch) : base(c, ch) { }
    protected override void InvalidateCache(User? entity, string? id, string listKey)
    {
        base.InvalidateCache(entity, id, listKey);
        if (id != null) _cache.RemoveAsync(CacheKeys.TopicsByUser(id));
    }
}



