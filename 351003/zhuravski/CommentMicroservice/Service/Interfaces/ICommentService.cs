using CommentMicroservice.Service.DTOs;

namespace CommentMicroservice.Service.Interfaces;

public interface ICommentService
{
    Task<CommentResponseDTO[]> GetAllCommentsAsync();
    Task<CommentResponseDTO> CreateCommentAsync(CommentRequestDTO dto);
    Task DeleteCommentAsync(Guid id);
    Task<CommentResponseDTO> GetCommentByIdAsync(Guid id);
    Task<CommentResponseDTO> UpdateCommentByIdAsync(Guid id, CommentRequestDTO dto);
}