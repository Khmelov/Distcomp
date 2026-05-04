using ServerApp.Models;
using ServerApp.Models.DTOs.Requests;
using ServerApp.Models.DTOs.Responses;

namespace ServerApp.Services.Interfaces;

public interface IAuthorService
{
    IEnumerable<AuthorResponseTo> GetPaged(QueryParams parameters);
    AuthorResponseTo GetById(long id);
    AuthorResponseTo Create(AuthorRequestTo request);
    AuthorResponseTo Update(long id, AuthorRequestTo request);
    void Delete(long id);
}