using Publisher.Application.DTOs.Abstractions;
using Publisher.Application.DTOs.Requests;
using Publisher.Application.DTOs.Responses;

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

public interface IAuthorService : IService<AuthorRequestTo, AuthorResponseTo> {
}

public interface IIssueService : IService<IssueRequestTo, IssueResponseTo> {
}

public interface ILabelService : IService<LabelRequestTo, LabelResponseTo> {
}

public interface ICommentService : IService<CommentRequestTo, CommentResponseTo> {
}