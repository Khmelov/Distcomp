using Cassandra;
using Discussion.Infrastructure.Abstractions;

namespace Discussion.Infrastructure.Migrations;

public class M001_CreateInitial : ICassandraMigration
{
    public string Id { get; } = "001_CreateInitial";
    
    public async Task UpAsync(ISession session)
    {
        var cqlCreateTable = @"
            CREATE TABLE IF NOT EXISTS tbl_reactions (
                topicId bigint,
                id bigint,
                country text,
                content varchar,
                primary key (topicId, id)
            );
            ";
        
        var cqlAddIndex = @" 
            CREATE CUSTOM INDEX IF NOT EXISTS ix_reactions_id 
            ON distcomp.tbl_reactions(id)
            USING 'StorageAttachedIndex';
            ";
        
        await session.ExecuteAsync(new SimpleStatement(cqlCreateTable));
        await session.ExecuteAsync(new SimpleStatement(cqlAddIndex));
        
    }
}
