using Domain.Models;

namespace Infrastructe.Repository;

public class LocalNoteRepository : BaseRepository<Note>
{
    public override async Task<Note?> UpdateAsync(long id, Note note)
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

    public override Note Copy(Note src)
    {
        return new Note()
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