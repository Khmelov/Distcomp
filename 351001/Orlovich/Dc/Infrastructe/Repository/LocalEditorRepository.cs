using Application.Repository;
using AutoMapper;
using Domain.Models;

namespace Infrastructe.Repository;

public class LocalEditorRepository : BaseRepository<Editor>
{
    public override async Task<Editor?> UpdateAsync(long id, Editor editor)
    {
        await Task.CompletedTask;
        var a = _storage.Find(a => a.id == id);
        
        if(a == null)
            return null;
        
        a.firstname = editor.firstname;
        a.lastname = editor.lastname;
        a.login = editor.login;
        a.password = editor.password;
        
        return Copy(a);
    }
    
    public override Editor Copy(Editor src)
    {
        return new Editor()
        {
            id = src.id,
            firstname = src.firstname,
            lastname = src.lastname,
            login = src.login,
            password = src.password
        };
    }
}