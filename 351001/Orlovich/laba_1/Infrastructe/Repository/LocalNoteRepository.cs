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
        
        return a;
    }
}