using Domain.Models;

namespace Infrastructe.Repository.LocalRepository;

public class LocalCommentRepository : BaseLocalRepository<Comment>
{
    public override async Task<Comment?> UpdateAsync(long id, Comment story)
    {
        await Task.CompletedTask;
        var a = _storage.Find(a => a.id == id);
        
        if(a == null)
            return null;
        
        a.content = story.content;
        a.issueId = story.issueId;
        
        return Copy(a);
    }

    public override Comment Copy(Comment src)
    {
        return new Comment()
        {
            id = src.id,
            content = src.content,
            issueId = src.issueId
        };
    }
}