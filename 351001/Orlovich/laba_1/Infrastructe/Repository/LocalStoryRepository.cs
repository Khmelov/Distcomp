using Domain.Models;

namespace Infrastructe.Repository;

public class LocalStoryRepository : BaseRepository<Story>
{
    public override async Task<Story?> UpdateAsync(long id, Story story)
    {
        await Task.CompletedTask;
        var a = _storage.Find(a => a.id == id);
        
        if(a == null)
            return null;
        
        a.content = story.content;
        a.issueId = story.issueId;
        
        return a;
    }
}