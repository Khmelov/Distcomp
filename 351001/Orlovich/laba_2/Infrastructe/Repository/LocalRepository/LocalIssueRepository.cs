using Domain.Models;

namespace Infrastructe.Repository.LocalRepository;

public class LocalIssueRepository : BaseLocalRepository<Issue>
{
    public override async Task<Issue?> UpdateAsync(long id, Issue note)
    {
        await Task.CompletedTask;
        var a = _storage.Find(a => a.id == id);
        
        if(a == null)
            return null;
        
        a.title = note.title;
        a.content = note.content;
        a.created = note.created;
        a.modified = note.modified;
        
        return Copy(a);
    }

    public override Issue Copy(Issue src)
    {
        return new Issue()
        {
            id = src.id,
            authorId =  src.authorId,
            title = src.title,
            content = src.content,
            created = src.created,
            modified = src.modified,
        };
    }
}