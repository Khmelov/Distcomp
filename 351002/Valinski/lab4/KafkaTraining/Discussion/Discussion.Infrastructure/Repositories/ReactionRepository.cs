using Cassandra;
using Discussion.Domain.Abstractions;
using Discussion.Domain.Models;
using IdGen;

namespace Discussion.Infrastructure.Repositories;

public class ReactionRepository : IReactionRepository
{
    private readonly ISession _session;
    private readonly PreparedStatement _stmt;
    private IdGenerator _idGenerator = new(0);
    
    public ReactionRepository(ISession session)
    {
        _session = session;
        string query = @"INSERT INTO distcomp.tbl_reactions (id, topicid, country, content) VALUES (?, ?, ?, ?)";
        _stmt = _session.Prepare(query);
    }
    
    public async Task<List<Reaction>> GetAllReactions()
    {
        string query = @"SELECT * FROM distcomp.tbl_reactions";
        var rowSet = _session.Execute(query);

        List<Reaction> reactions = new List<Reaction>();
        
        foreach (var row in rowSet)
        {
            reactions.Add(new Reaction()
            {
                Id = row.GetValue<long>("id"),
                Country = row.GetValue<string>("country"),
                TopicId = row.GetValue<long>("topicid"), 
                Content = row.GetValue<string>("content")
            });
        }

        return reactions;
    }

    public async Task<Reaction> CreateReaction(Reaction reaction)
    {
        var bind = _stmt.Bind(reaction.Id, reaction.TopicId, reaction.Country, reaction.Content);
        await _session.ExecuteAsync(bind);
        return reaction;
    }

    public async Task<Reaction?> GetById(long id)
    {
        var query = "SELECT * FROM distcomp.tbl_reactions WHERE id = ?";
        var selectStmt = new SimpleStatement(query, id);
        
        var rowSet = await _session.ExecuteAsync(selectStmt);
        
        if (rowSet.IsExhausted())
        {
            return null;
        }

        var row = rowSet.GetRows().First();
        return new Reaction()
        {
            Id = row.GetValue<long>("id"),
            Country = row.GetValue<string>("country"),
            TopicId = row.GetValue<long>("topicid"), 
            Content = row.GetValue<string>("content")
        };

    }

    public async Task<Reaction> UpdateReaction(Reaction reaction)
    {
        var updateStmt = _stmt.Bind(reaction.Id, reaction.TopicId, reaction.Country, reaction.Content);
        await _session.ExecuteAsync(updateStmt);
        return reaction;
    }

    public async Task DeleteReaction(long id)
    {
        var query = "DELETE FROM distcomp.tbl_reactions WHERE id = ?";
        var deleteStmt = new SimpleStatement(query, id);
        await _session.ExecuteAsync(deleteStmt);
 
    }
}
