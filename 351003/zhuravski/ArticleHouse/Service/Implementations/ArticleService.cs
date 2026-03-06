using Additions.Exceptions;
using ArticleHouse.DAO;
using ArticleHouse.DAO.Models;
using ArticleHouse.Service.DTOs;
using ArticleHouse.Service.Interfaces;
using Microsoft.EntityFrameworkCore;

namespace ArticleHouse.Service.Implementations;

public class ArticleService : Service, IArticleService
{
    private readonly ApplicationContext db;
    private readonly IMarkService markService;

    public ArticleService(ApplicationContext db, IMarkService markService)
    {
        this.db = db;
        this.markService = markService;
    }

    public async Task<ArticleResponseDTO[]> GetAllArticlesAsync()
    {
        ArticleModel[] models = await db.Articles.ToArrayAsync();
        return [.. models.Select(MakeResponseFromModel)];
    }

    public async Task<ArticleResponseDTO> CreateArticleAsync(ArticleRequestDTO dto)
    {
        //Пока предположим, что хеш-теги можно добавлять только при создании.
        long[]? markIds = null;
        if (null != dto.Marks)
        {
            markIds = await markService.ReserveMarkIdsByNamesAsync(dto.Marks);
        }
        ArticleModel model = MakeModelFromRequest(dto);
        await db.Articles.AddAsync(model);
        
        if (null != markIds) {
            foreach (long markId in markIds)
            {
                model.ArticleMarks.Add(new ArticleMark
                {
                    Article = model,
                    MarkId = markId
                });
            }
        }
        await InvokeDAOMethod(() => db.SaveChangesAsync());

        return MakeResponseFromModel(model);
    }

    public async Task<ArticleResponseDTO> GetArticleByIdAsync(long id)
    {
        ArticleModel? model = await db.Articles.FirstOrDefaultAsync(o => o.Id == id);
        if (null == model)
        {
            throw new ServiceObjectNotFoundException();
        }
        return MakeResponseFromModel(model);
    }

    public async Task DeleteArticleAsync(long id)
    {
        //Какой богомерзкий API.
        ArticleModel? model = await db.Articles.Include(a => a.ArticleMarks).FirstOrDefaultAsync(o => o.Id == id);
        if (null == model)
        {
            throw new ServiceObjectNotFoundException();
        }
        long[] leftMarkIds = [.. model.ArticleMarks.Select(m => m.MarkId)];
        db.Articles.Remove(model);
        await InvokeDAOMethod(() => db.SaveChangesAsync());
        await markService.ReleaseLeftMarksByIdsAsync(leftMarkIds);
    }

    public async Task<ArticleResponseDTO> UpdateArticleByIdAsync(long id, ArticleRequestDTO dto)
    {
        if (null == dto.Id)
        {
            throw new ServiceException();
        }
        ArticleModel? model = await db.Articles.FirstOrDefaultAsync(o => o.Id == dto.Id);
        if (null == model) {
            throw new ServiceObjectNotFoundException();
        }
        ShapeModelFromRequest(ref model, dto);
        await InvokeDAOMethod(() => db.SaveChangesAsync());
        return MakeResponseFromModel(model);
    }

    private static ArticleModel MakeModelFromRequest(ArticleRequestDTO dto)
    {
        ArticleModel result = new();
        ShapeModelFromRequest(ref result, dto);
        return result;
    }

    private static void ShapeModelFromRequest(ref ArticleModel model, ArticleRequestDTO dto)
    {
        model.Id = dto.Id ?? 0;
        model.CreatorId = dto.CreatorId;
        model.Title = dto.Title;
        model.Content = dto.Content;
    }

    private static ArticleResponseDTO MakeResponseFromModel(ArticleModel model)
    {
        return new ArticleResponseDTO()
        {
            Id = model.Id,
            CreatorId = model.CreatorId,
            Title = model.Title,
            Content = model.Content
        };
    }
}