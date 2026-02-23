using ArticleHouse.Service.DTOs;

namespace ArticleHouse.Service.Interfaces;

public interface ICommentService
{
    Task<CommentResponseDTO[]> GetAllCommentsAsync();
    Task<CommentResponseDTO> CreateCommentAsync(CommentRequestDTO dto);
    Task DeleteCommentAsync(long id);
    Task<CommentResponseDTO> GetCommentByIdAsync(long id);
    Task<CommentResponseDTO> UpdateCommentByIdAsync(long id, CommentRequestDTO dto);
}