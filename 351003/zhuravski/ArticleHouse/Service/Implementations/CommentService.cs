using ArticleHouse.DAO;
using ArticleHouse.DAO.Models;
using ArticleHouse.Service.DTOs;
using ArticleHouse.Service.Exceptions;
using ArticleHouse.Service.Interfaces;
using Microsoft.EntityFrameworkCore;

namespace ArticleHouse.Service.Implementations;

public class CommentService : Service, ICommentService
{
    private readonly ApplicationContext db;
    public CommentService(ApplicationContext db)
    {
        this.db = db;
    }
    public async Task<CommentResponseDTO> CreateCommentAsync(CommentRequestDTO dto)
    {
        CommentModel model = MakeModelFromRequest(dto);
        await db.Comments.AddAsync(model);
        await InvokeDAOMethod(() => db.SaveChangesAsync());
        return MakeResponseFromModel(model);
    }

    public async Task DeleteCommentAsync(long id)
    {
        CommentModel? model = await db.Comments.FirstOrDefaultAsync(o => o.Id == id);
        if (null == model)
        {
            throw new ServiceObjectNotFoundException();
        }
        db.Comments.Remove(model);
        await InvokeDAOMethod(() => db.SaveChangesAsync());
    }

    public async Task<CommentResponseDTO[]> GetAllCommentsAsync()
    {
        CommentModel[] models = await db.Comments.ToArrayAsync();
        return [.. models.Select(MakeResponseFromModel)];
    }

    public async Task<CommentResponseDTO> GetCommentByIdAsync(long id)
    {
        CommentModel? model = await db.Comments.FirstOrDefaultAsync(o => o.Id == id);
        if (null == model)
        {
            throw new ServiceObjectNotFoundException();
        }
        return MakeResponseFromModel(model);
    }

    public async Task<CommentResponseDTO> UpdateCommentByIdAsync(long id, CommentRequestDTO dto)
    {
        if (null == dto.Id)
        {
            throw new ServiceException();
        }
        CommentModel? model = await db.Comments.FirstOrDefaultAsync(o => o.Id == dto.Id);
        if (null == model) {
            throw new ServiceObjectNotFoundException();
        }
        ShapeModelFromRequest(ref model, dto);
        await InvokeDAOMethod(() => db.SaveChangesAsync());
        return MakeResponseFromModel(model);
    }

    private static CommentModel MakeModelFromRequest(CommentRequestDTO dto)
    {
        CommentModel result = new();
        ShapeModelFromRequest(ref result, dto);
        return result;
    }

    private static void ShapeModelFromRequest(ref CommentModel model, CommentRequestDTO dto)
    {
        model.Id = dto.Id ?? 0;
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
}