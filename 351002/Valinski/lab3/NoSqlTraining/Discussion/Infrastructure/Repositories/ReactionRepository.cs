using Cassandra;
using Cassandra.Mapping;
using Domain.Abstractions;
using Domain.Models;
using IdGen;

namespace Infrastructure.Repositories;

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
        reaction.Id = _idGenerator.CreateId();
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

    public async Task DeleteReaction(long id, long topicid, string country)
    {
        
        var query = "DELETE FROM distcomp.tbl_reactions WHERE id = ? AND topicid = ? AND country = ?";
        var deleteStmt = new SimpleStatement(query, id, topicid, country);
        await _session.ExecuteAsync(deleteStmt);
 
    }
}
