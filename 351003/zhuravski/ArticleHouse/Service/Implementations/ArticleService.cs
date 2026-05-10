using System.Text.Json;
using Additions.Cache.Interfaces;
using Additions.Messaging.Interfaces;
using Additions.Service;
using ArticleHouse.DAO.Interfaces;
using ArticleHouse.DAO.Models;
using ArticleHouse.Service.DTOs;
using ArticleHouse.Service.Interfaces;
using CommonAPI.Service.Events;

namespace ArticleHouse.Service.Implementations;

public class ArticleService : BasicService, IArticleService
{
    private readonly IArticleDAO dao;
    private readonly IArticleMarkDAO m2mDAO;
    private readonly IMarkDAO markDAO;
    private readonly IEventProducer eventProducer;
    private readonly IDistributedCache cache;
    private readonly string eventTopic;

    public ArticleService(IArticleDAO dao, IArticleMarkDAO m2mDAO, IMarkDAO markDAO,
                          IEventProducer eventProducer, IDistributedCache cache, IConfiguration configuration)
    {
        this.dao = dao;
        this.m2mDAO = m2mDAO;
        this.markDAO = markDAO;
        this.eventProducer = eventProducer;
        this.cache = cache;
        eventTopic = configuration["Kafka:SendTopic"] ?? "default-topic";
    }

    public async Task<ArticleResponseDTO[]> GetAllArticlesAsync()
    {
        ArticleModel[] models = await InvokeDAOMethod(() => dao.GetAllAsync());
        return [.. models.Select(MakeResponseFromModel)];
    }

    public async Task<ArticleResponseDTO> CreateArticleAsync(ArticleRequestDTO dto)
    {
        long[]? markIds = null;
        if (null != dto.Marks)
        {
            markIds = await InvokeDAOMethod(() => markDAO.ReserveIdsByNamesAsync(dto.Marks));
        }
        ArticleModel model = MakeModelFromRequest(dto);
        ArticleModel result = await InvokeDAOMethod(() => dao.AddNewAsync(model));
        
        if (null != markIds) {
            await InvokeDAOMethod(() => m2mDAO.LinkArticleWithMarksAsync(result.Id, markIds));
        }

        return MakeResponseFromModel(result);
    }

    public async Task<ArticleResponseDTO> GetArticleByIdAsync(long id)
    {
        return await cache.GetOrSetAsync(
            $"article:{id}",
            async () =>
            {
                ArticleModel model = await InvokeDAOMethod(() => dao.GetByIdAsync(id));
                return MakeResponseFromModel(model);
            },
            TimeSpan.FromMinutes(10)
        );
    }

    public async Task DeleteArticleAsync(long id)
    {
        var result = await InvokeDAOMethod(() => dao.GetByIdWithMarksAsync(id));
        long[] leftMarkIds = result.Item2;
        
        await InvokeLowerMethod(async () =>
        {
            await dao.DeleteAsync(id);
            await eventProducer.ProduceEventAsync(eventTopic, new EventMessage()
            {
                Operation = EventNames.ARTICLE_DELETED,
                Payload = JsonSerializer.Serialize(id)
            });
            await markDAO.ReleaseByIdsAsync(leftMarkIds);
        });
        
        await cache.RemoveAsync($"article:{id}");
    }

    public async Task<ArticleResponseDTO> UpdateArticleByIdAsync(long id, ArticleRequestDTO dto)
    {
        ArticleModel model = MakeModelFromRequest(dto);
        model.Id = id;
        ArticleModel result = await InvokeDAOMethod(() => dao.UpdateAsync(model));
        
        await cache.RemoveAsync($"article:{id}");
        
        return MakeResponseFromModel(result);
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