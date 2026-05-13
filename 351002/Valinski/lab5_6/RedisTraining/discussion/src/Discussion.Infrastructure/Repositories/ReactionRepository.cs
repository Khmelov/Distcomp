using Cassandra;
using Cassandra.Mapping;
using Discussion.Application.Repositories;
using Discussion.Domain.Models;

namespace Discussion.Infrastructure.Repositories;

public class ReactionRepository : IReactionRepository
{
    private readonly ISession _session;
    private readonly Mapper _mapper;

    public ReactionRepository(ISession session)
    {
        _session = session;
        _mapper = new Mapper(session);
    }

    public async Task<Reaction?> GetByIdAsync(long id)
    {
        var reactionId = _mapper.FirstOrDefault<ReactionId>("SELECT * FROM tbl_id_topicId WHERE id = ?", id);
        if (reactionId == null) return null;
        var reaction = _mapper.FirstOrDefault<Reaction>("SELECT * FROM tbl_reactions WHERE topicid = ? and id = ?",
            reactionId.TopicId, reactionId.Id);
        return reaction;
    }

    public async Task<List<Reaction>> GetAllAsync()
    {
        List<Reaction> reactionsFromDb = (await _mapper.FetchAsync<Reaction>("SELECT * FROM tbl_reactions")).ToList();

        var temp = reactionsFromDb.ToList();
        return temp;
    }

    public async Task AddAsync(Reaction entity)
    {
        var stmt = new SimpleStatement(
            "INSERT INTO tbl_reactions (topicid, id, country, content) VALUES (:t, :i, :c, :content)",
            new
            {
                t = entity.TopicId,
                i = entity.Id,
                c = entity.Country,
                content = entity.Content
            }
        );

        var idStmt = new SimpleStatement(
            "INSERT INTO tbl_id_topicId (topicid, id) VALUES (?,?)", entity.TopicId, entity.Id);

        await _session.ExecuteAsync(stmt);
        await _session.ExecuteAsync(idStmt);
    }

    public async Task<Reaction> UpdateAsync(Reaction entity)
    {
        var stmt = new SimpleStatement(
            "UPDATE tbl_reactions SET country = ?, content = ? WHERE topicid = ? and id = ?",
            entity.Country, entity.Content, entity.TopicId, entity.Id
        );
        
        await _session.ExecuteAsync(stmt);
        return entity;
    }

    public async Task DeleteAsync(Reaction entity)
    {
        var stmt = new SimpleStatement(
            "DELETE FROM tbl_reactions WHERE topicid = ? and id = ?",
            entity.TopicId, entity.Id
            );
        
        await _session.ExecuteAsync(stmt);
    }
}
