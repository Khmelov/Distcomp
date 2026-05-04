using Mapster;
using ServerApp.Models;
using ServerApp.Models.DTOs.Requests;
using ServerApp.Models.DTOs.Responses;
using ServerApp.Models.Entities;
using ServerApp.Repository;
using ServerApp.Services.Interfaces;

namespace ServerApp.Services.Implementations;

public class ArticleService(
    IRepository<Sticker> stickerRepo,
    IRepository<Article> articleRepo,
    IRepository<Author> authorRepo) : IArticleService
{
    public IEnumerable<ArticleResponseTo> GetPaged(QueryParams parametrs) => 
        articleRepo.GetPaged(parametrs).Adapt<IEnumerable<ArticleResponseTo>>();

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
        
        bool titleExists = articleRepo.GetAll()
            .Any(a => a.Title.Equals(request.Title, StringComparison.OrdinalIgnoreCase));
        
        if (titleExists)
        {
            throw new InvalidOperationException($"Title '{request.Title}' is already taken");
        }  

        var article = request.Adapt<Article>();
        article.Created = article.Modified = DateTime.UtcNow;
        
        article.Stickers = new List<Sticker>();
        
        if (request.Stickers != null && request.Stickers.Any())
        {
            var existingStickers = stickerRepo.GetAll().ToList(); 

            foreach (string stickerName in request.Stickers)
            {
                var found = existingStickers.FirstOrDefault(s => s.Name == stickerName);
                
                if (found != null)
                {
                    article.Stickers.Add(found);
                }
                else
                {
                    article.Stickers.Add(new Sticker { Name = stickerName });
                }
            }
        }
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
        
        if (request.Stickers != null)
        {
            var allStickersInDb = stickerRepo.GetAll().ToList();
            existing.Stickers.Clear();
            foreach (var stickerName in request.Stickers)
            {
                var foundSticker = allStickersInDb.FirstOrDefault(s => s.Name == stickerName);
            
                if (foundSticker != null)
                {
                    existing.Stickers.Add(foundSticker);
                }
                else
                {
                    var newSticker = new Sticker { Name = stickerName };
                    existing.Stickers.Add(newSticker);
                    
                    allStickersInDb.Add(newSticker); 
                }
            }
        }
        
        articleRepo.Update(existing);
        return existing.Adapt<ArticleResponseTo>();
    }

    public void Delete(long id)
    {
        var article = articleRepo.GetById(id) ?? throw new KeyNotFoundException($"Article {id} not found");
        var stickersToCheck = article.Stickers.ToList();
        articleRepo.Delete(id);
        
        foreach (var sticker in stickersToCheck)
        {
            var currentSticker = stickerRepo.GetById(sticker.Id);
            if (currentSticker != null && currentSticker.Articles.Count == 0)
            {
                stickerRepo.Delete(currentSticker.Id);
            }
        }
    }
}