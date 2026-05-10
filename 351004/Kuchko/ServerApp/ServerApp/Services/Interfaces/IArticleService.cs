using ServerApp.Models.DTOs.Requests;
using ServerApp.Models.DTOs.Responses;

namespace ServerApp.Services.Interfaces;

public interface IArticleService
{
    IEnumerable<ArticleResponseTo> GetAll();
    ArticleResponseTo GetById(long id);
    ArticleResponseTo Create(ArticleRequestTo request);
    ArticleResponseTo Update(long id, ArticleRequestTo request);
    void Delete(long id);
}