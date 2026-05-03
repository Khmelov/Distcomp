using Cassandra;
using Cassandra.Mapping;
using Discussion.Domain.Entities;
using Discussion.Domain.Interfaces;

namespace Discussion.Infrastructure.Repositories;

public class CommentRepository : ICommentRepository
{
    private readonly IMapper _mapper;

    public CommentRepository(ISession session)
    {
        _mapper = new Mapper(session);
    }

    public async Task<IEnumerable<Comment>> GetAllAsync()
    {
        return await _mapper.FetchAsync<Comment>();
    }

    public async Task<Comment?> GetByIdAsync(long id)
    {
        return await _mapper.FirstOrDefaultAsync<Comment>("WHERE id = ? ALLOW FILTERING", id);
    }

    public async Task<IEnumerable<Comment>> GetByIssueIdAsync(long issueId)
    {
        return await _mapper.FetchAsync<Comment>("WHERE issue_id = ?", issueId);
    }

    public async Task DeleteByIssueIdAsync(long issueId)
    {
        await _mapper.DeleteAsync<Comment>("WHERE issue_id = ?", issueId);
    }

    public async Task<Comment> CreateAsync(Comment entity)
    {
        try
        {
            await _mapper.InsertAsync(entity);
        }
        catch (Exception ex)
        {
        }

        return entity;
    }

    public async Task<Comment?> UpdateAsync(Comment entity)
    {
        await _mapper.UpdateAsync(entity);
        return entity;
    }

    public async Task<bool> DeleteAsync(long id)
    {
        var existing = await _mapper.FirstOrDefaultAsync<Comment>("WHERE id = ? ALLOW FILTERING", id);

        if (existing == null) return false;

        await _mapper.DeleteAsync<Comment>("WHERE issue_id = ? AND id = ?", existing.IssueId, id);
        return true;
    }
}