using Shared.DTOs.Abstractions;
using Shared.DTOs.Requests;
using Shared.DTOs.Responses;

namespace Discussion.Application.Services.Interfaces;

public interface IService<TRequest, TResponse>
    where TRequest : BaseRequestTo
    where TResponse : BaseResponseTo
{
    Task<IEnumerable<TResponse>> GetAllAsync();
    Task<TResponse> GetByIdAsync(long id);
    Task<TResponse> CreateAsync(TRequest request);

    Task<TResponse> UpdateAsync(TRequest request);

    Task<bool> DeleteAsync(long id);
}

public interface ICommentService : IService<CommentRequestTo, CommentResponseTo>
{
    public Task<IEnumerable<CommentResponseTo>> GetByIssueIdAsync(long issueId);

    public Task DeleteByIssueIdAsync(long issueId);
}