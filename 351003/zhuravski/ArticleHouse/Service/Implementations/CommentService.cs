using ArticleHouse.DAO.Interfaces;
using ArticleHouse.DAO.Models;
using ArticleHouse.Service.DTOs;
using ArticleHouse.Service.Interfaces;

namespace ArticleHouse.Service.Implementations;

public class CommentService : Service, ICommentService
{
    private readonly ICommentDAO commentDAO;
    public CommentService(ICommentDAO commentDAO)
    {
        this.commentDAO = commentDAO;
    }
    public async Task<CommentResponseDTO> CreateCommentAsync(CommentRequestDTO dto)
    {
        CommentModel model = MakeModelFromRequest(dto);
        CommentModel result = await InvokeDAOMethod(() => commentDAO.AddNewAsync(model));
        return MakeResponseFromModel(result);
    }

    public async Task DeleteCommentAsync(long id)
    {
        await InvokeDAOMethod(() => commentDAO.DeleteAsync(id));
    }

    public async Task<CommentResponseDTO[]> GetAllCommentsAsync()
    {
        CommentModel[] daoModels = await InvokeDAOMethod(() => commentDAO.GetAllAsync());
        return [.. daoModels.Select(MakeResponseFromModel)];
    }

    public async Task<CommentResponseDTO> GetCommentByIdAsync(long id)
    {
        CommentModel model = await InvokeDAOMethod(() => commentDAO.GetByIdAsync(id));
        return MakeResponseFromModel(model);
    }

    public async Task<CommentResponseDTO> UpdateCommentByIdAsync(long id, CommentRequestDTO dto)
    {
        CommentModel model = MakeModelFromRequest(dto);
        model.Id = id;
        CommentModel result = await InvokeDAOMethod(() => commentDAO.UpdateAsync(model));
        return MakeResponseFromModel(result);
    }

    private static CommentModel MakeModelFromRequest(CommentRequestDTO dto)
    {
        return new CommentModel()
        {
            Id = dto.Id ?? 0,
            ArticleId = dto.ArticleId,
            Content = dto.Content
        };
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
}