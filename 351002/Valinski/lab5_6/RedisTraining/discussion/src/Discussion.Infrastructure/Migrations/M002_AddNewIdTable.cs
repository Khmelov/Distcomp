using Cassandra;
using Discussion.Infrastructure.Abstractions;

namespace Discussion.Infrastructure.Migrations;

public class M002_AddNewIdTable : ICassandraMigration
{
    public string Id { get; } = "M002_AddNewIdTable"; 
    public async Task UpAsync(ISession session)
    {
        var cql = @"CREATE TABLE IF NOT EXISTS tbl_id_topicId(
                        id bigint,
                        topicid bigint,
                        primary key(id));";
        var stmt = new SimpleStatement(cql);
        
        await session.ExecuteAsync(stmt);
    }
}
