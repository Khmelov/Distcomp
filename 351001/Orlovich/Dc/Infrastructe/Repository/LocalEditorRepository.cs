using Application.Repository;
using AutoMapper;
using Domain.Models;

namespace Infrastructe.Repository;

public class LocalEditorRepository : IRepository<Editor>
{
    private readonly List<Editor> _storage = new();
    
    public async Task<IList<Editor>> GetAllAsync()
    {
        await Task.CompletedTask;
        return _storage.ToList();
    }

    public async Task<Editor> GetByIdAsync(long id)
    {
        await Task.CompletedTask;
        var src =_storage.Find(a => a.id == id);
        
        if(src == null)
            return null;

        return Copy(src);
    }

    public async Task AddAsync(Editor editor)
    {
        await Task.CompletedTask;
        _storage.Add(editor);
    }

    public async Task<Editor?> UpdateAsync(long id, Editor editor)
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

    public async Task<int> DeleteAsync(long id)
    {
        await Task.CompletedTask;
        
        var count = _storage.RemoveAll(a => a.id == id);
        return count;
    }


    private static Editor Copy(Editor src)
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