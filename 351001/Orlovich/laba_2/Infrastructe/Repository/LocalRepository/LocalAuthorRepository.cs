using Application.Repository;
using AutoMapper;
using Domain.Models;

namespace Infrastructe.Repository.LocalRepository;

public class LocalAuthorRepository : BaseLocalRepository<Author>
{
    public override async Task<Author?> UpdateAsync(long id, Author editor)
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
    
    public override Author Copy(Author src)
    {
        return new Author()
        {
            id = src.id,
            firstname = src.firstname,
            lastname = src.lastname,
            login = src.login,
            password = src.password
        };
    }
}