using Publisher.Application.DTOs.Requests;
using Publisher.Application.DTOs.Responses;
using Shared.DTOs.Abstractions;
using Shared.DTOs.Requests;
using Shared.DTOs.Responses;

namespace Publisher.Application.Services.Interfaces;

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

public interface IAuthorService : IService<AuthorRequestTo, AuthorResponseTo>
{
}

public interface IIssueService : IService<IssueRequestTo, IssueResponseTo>
{
}

public interface ILabelService : IService<LabelRequestTo, LabelResponseTo>
{
}

public interface ICommentService : IService<CommentRequestTo, CommentResponseTo>
{
    public Task<IEnumerable<CommentResponseTo>> GetByIssueIdAsync(long issueId);

    public Task DeleteByIssueIdAsync(long issueId);
}

public interface IAuthService
{
    Task<AuthResponseTo> LoginAsync(LoginRequestTo request);

    Task<AuthorResponseTo> RegisterAsync(AuthorRequestTo request);
}