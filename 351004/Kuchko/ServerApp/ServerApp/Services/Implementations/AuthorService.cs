using Mapster;
using ServerApp.Models.DTOs.Requests;
using ServerApp.Models.DTOs.Responses;
using ServerApp.Models.Entities;
using ServerApp.Repository;
using ServerApp.Services.Interfaces;

namespace ServerApp.Services.Implementations;

public class AuthorService(IRepository<Author> repository) : IAuthorService
{
    public IEnumerable<AuthorResponseTo> GetAll() => 
        repository.GetAll().Adapt<IEnumerable<AuthorResponseTo>>();

    public AuthorResponseTo GetById(long id)
    {
        var author = repository.GetById(id) ?? throw new KeyNotFoundException($"Author with ID {id} not found");
        return author.Adapt<AuthorResponseTo>();
    }

    public AuthorResponseTo Create(AuthorRequestTo request)
    {
        var author = request.Adapt<Author>();
        var created = repository.Create(author);
        return created.Adapt<AuthorResponseTo>();
    }

    public AuthorResponseTo Update(long id, AuthorRequestTo request)
    {
        var existing = repository.GetById(id) ?? throw new KeyNotFoundException($"Author {id} not found");
        request.Adapt(existing); 
        existing.Id = id;
        repository.Update(existing);
        return existing.Adapt<AuthorResponseTo>();
    }

    public void Delete(long id)
    {
        if (!repository.Delete(id)) throw new KeyNotFoundException($"Author {id} not found");
    }
}