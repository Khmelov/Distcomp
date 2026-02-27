using Domain.Models;

namespace Infrastructe.Repository;

public class LocalTagRepository : BaseRepository<Tag>
{
    public override async Task<Tag?> UpdateAsync(long id, Tag tag)
    {
        await Task.CompletedTask;
        var a = _storage.Find(a => a.id == id);
        
        if(a == null)
            return null;
        
        a.name = tag.name;
        
        return a;
    }
}