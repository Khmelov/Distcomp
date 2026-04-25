using Mapster;
using ServerApp.Models.DTOs.Requests;
using ServerApp.Models.DTOs.Responses;
using ServerApp.Models.Entities;
using ServerApp.Repository;
using ServerApp.Services.Interfaces;

namespace ServerApp.Services.Implementations;

public class ArticleService(
    IRepository<Article> articleRepo, 
    IRepository<Author> authorRepo) : IArticleService
{
    public IEnumerable<ArticleResponseTo> GetAll() => 
        articleRepo.GetAll().Adapt<IEnumerable<ArticleResponseTo>>();

    public ArticleResponseTo GetById(long id)
    {
        var article = articleRepo.GetById(id) ?? throw new KeyNotFoundException($"Article {id} not found");
        return article.Adapt<ArticleResponseTo>();
    }

    public ArticleResponseTo Create(ArticleRequestTo request)
    {
        // Валидация связи: существует ли автор?
        if (authorRepo.GetById(request.AuthorId) == null)
            throw new ArgumentException($"Author with ID {request.AuthorId} not found");

        var article = request.Adapt<Article>();
        article.Created = article.Modified = DateTime.UtcNow; // Установка меток времени
        
        var created = articleRepo.Create(article);
        return created.Adapt<ArticleResponseTo>();
    }

    public ArticleResponseTo Update(long id, ArticleRequestTo request)
    {
        var existing = articleRepo.GetById(id) ?? throw new KeyNotFoundException($"Article {id} not found");
        
        if (authorRepo.GetById(request.AuthorId) == null)
            throw new ArgumentException($"Author {request.AuthorId} not found");

        request.Adapt(existing);
        existing.Id = id;
        existing.Modified = DateTime.UtcNow; // Обновляем только дату изменения
        
        articleRepo.Update(existing);
        return existing.Adapt<ArticleResponseTo>();
    }

    public void Delete(long id)
    {
        if (!articleRepo.Delete(id)) throw new KeyNotFoundException($"Article {id} not found");
    }
}