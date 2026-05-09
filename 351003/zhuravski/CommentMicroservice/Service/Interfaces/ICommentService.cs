using CommentMicroservice.Service.DTOs;

namespace CommentMicroservice.Service.Interfaces;

public interface ICommentService
{
    Task<CommentResponseDTO[]> GetAllCommentsAsync();
    Task<CommentResponseDTO> CreateCommentAsync(CommentRequestDTO dto);
    Task DeleteCommentAsync(long id);
    Task DeleteCommentsByArticleIdAsync(long articleId);
    Task<CommentResponseDTO> GetCommentByIdAsync(long id);
    Task<CommentResponseDTO> UpdateCommentByIdAsync(long id, CommentRequestDTO dto);
}