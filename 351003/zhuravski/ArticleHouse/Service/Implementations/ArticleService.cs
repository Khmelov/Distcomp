using ArticleHouse.DAO.Interfaces;
using ArticleHouse.DAO.Models;
using ArticleHouse.Service.DTOs;
using ArticleHouse.Service.Interfaces;

namespace ArticleHouse.Service.Implementations;

public class ArticleService : Service, IArticleService
{
    private readonly IArticleDAO articleDAO;

    public ArticleService(IArticleDAO articleDAO)
    {
        this.articleDAO = articleDAO;
    }

    public async Task<ArticleResponseDTO[]> GetAllArticlesAsync()
    {
        ArticleModel[] models = await InvokeDAOMethod(() => articleDAO.GetAllAsync());
        return [.. models.Select(MakeResponseFromModel)];
    }

    public async Task<ArticleResponseDTO> CreateArticleAsync(ArticleRequestDTO dto)
    {
        ArticleModel model = MakeModelFromRequest(dto);
        ArticleModel result = await InvokeDAOMethod(() => articleDAO.AddNewAsync(model));
        return MakeResponseFromModel(result);
    }

    public async Task<ArticleResponseDTO> GetArticleByIdAsync(long id)
    {
        ArticleModel model = await InvokeDAOMethod(() => articleDAO.GetByIdAsync(id));
        return MakeResponseFromModel(model);
    }

    public async Task DeleteArticleAsync(long id)
    {
        await InvokeDAOMethod(() => articleDAO.DeleteAsync(id));
    }

    public async Task<ArticleResponseDTO> UpdateArticleByIdAsync(long id, ArticleRequestDTO dto)
    {
        ArticleModel model = MakeModelFromRequest(dto);
        model.Id = id;
        ArticleModel result = await InvokeDAOMethod(() => articleDAO.UpdateAsync(model));
        return MakeResponseFromModel(result);
    }

    private static ArticleModel MakeModelFromRequest(ArticleRequestDTO dto)
    {
        return new ArticleModel()
        {
            Id = dto.Id ?? 0,
            CreatorId = dto.CreatorId,
            Title = dto.Title,
            Content = dto.Content
        };
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