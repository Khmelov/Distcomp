using Publisher.Application.Clients;
using Publisher.Application.DTOs.Requests;
using Publisher.Application.DTOs.Responses;
using Publisher.Application.Exceptions;
using Publisher.Application.Services.Interfaces;
using Publisher.Domain.Interfaces;

namespace Publisher.Application.Services;

public class RemoteCommentService : ICommentService
{
    private readonly DiscussionClient _client;
    private readonly IIssueRepository _issueRepository;

    public RemoteCommentService(DiscussionClient client, IIssueRepository issueRepository)
    {
        _client = client;
        _issueRepository = issueRepository;
    }

    public async Task<IEnumerable<CommentResponseTo>> GetAllAsync()
    {
        return await _client.GetByIssueIdAsync(0);
    }

    public async Task<CommentResponseTo> GetByIdAsync(long id)
    {
        var result = await _client.GetByIdAsync(id);
        return result;
    }

    public async Task<CommentResponseTo> CreateAsync(CommentRequestTo request)
    {
        var issueExists = await _issueRepository.GetByIdAsync(request.IssueId);
        if (issueExists == null)
        {
            throw new RestException(400, 27, $"Issue with id {request.IssueId} does not exist.");
        }

        return await _client.CreateAsync(request);
    }

    public async Task<CommentResponseTo> UpdateAsync(CommentRequestTo request)
    {
        var result = await _client.UpdateAsync(request);
        return result;
    }

    public async Task<bool> DeleteAsync(long id)
    {
        var isDeleted = await _client.DeleteAsync(id);
        
        if (!isDeleted)
        {
            throw new RestException(404, 45, "Comment not found in remote service");
        }

        return true;
    }

    public async Task<IEnumerable<CommentResponseTo>> GetByIssueIdAsync(long issueId)
    {
        var results = await _client.GetByIssueIdAsync(issueId);
        return results;
    }

    public async Task DeleteByIssueIdAsync(long issueId)
    {
        await _client.DeleteByIssueIdAsync(issueId);
    }
    
}