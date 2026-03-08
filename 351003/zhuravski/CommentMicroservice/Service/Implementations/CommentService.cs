using Additions.Service;
using CommentMicroservice.DAO.Interfaces;
using CommentMicroservice.DAO.Models;
using CommentMicroservice.Service.DTOs;
using CommentMicroservice.Service.Interfaces;

namespace CommentMicroservice.Service.Implementations;

public class CommentService : BasicService, ICommentService
{
    private readonly ICommentDAO dao;
    public CommentService(ICommentDAO dao)
    {
        this.dao = dao;
    }
    public async Task<CommentResponseDTO> CreateCommentAsync(CommentRequestDTO dto)
    {
        CommentModel model = MakeModelFromRequest(dto);
        CommentModel result = await InvokeDAOMethod(() => dao.AddNewAsync(model));
        return MakeResponseFromModel(result);
    }

    public async Task DeleteCommentAsync(long id)
    {
        await InvokeDAOMethod(() => dao.DeleteAsync(id));
    }

    public async Task<CommentResponseDTO[]> GetAllCommentsAsync()
    {
        CommentModel[] daoModels = await InvokeDAOMethod(() => dao.GetAllAsync());
        return [.. daoModels.Select(MakeResponseFromModel)];
    }

    public async Task<CommentResponseDTO> GetCommentByIdAsync(long id)
    {
        CommentModel model = await InvokeDAOMethod(() => dao.GetByIdAsync(id));
        return MakeResponseFromModel(model);
    }

    public async Task<CommentResponseDTO> UpdateCommentByIdAsync(long id, CommentRequestDTO dto)
    {
        CommentModel model = MakeModelFromRequest(dto);
        model.Id = id;
        CommentModel result = await InvokeDAOMethod(() => dao.UpdateAsync(model));
        return MakeResponseFromModel(result);
    }

    private static CommentModel MakeModelFromRequest(CommentRequestDTO dto)
    {
        CommentModel result = new();
        ShapeModelFromRequest(ref result, dto);
        return result;
    }

    private static void ShapeModelFromRequest(ref CommentModel model, CommentRequestDTO dto)
    {
        model.Id = dto.Id ?? default!;
        model.ArticleId = dto.ArticleId;
        model.Content = dto.Content;
    }

    private static CommentResponseDTO MakeResponseFromModel(CommentModel model)
    {
        return new CommentResponseDTO()
        {
            Id = model.Id,
            ArticleId = model.ArticleId,
            Content = model.Content
        };
    }

    public async Task DeleteCommentsByArticleIdAsync(long articleId)
    {
        await InvokeDAOMethod(() => dao.DeleteByArticleIdAsync(articleId));
    }
}