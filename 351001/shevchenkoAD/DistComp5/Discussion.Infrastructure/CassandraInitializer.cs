using Cassandra;

namespace Discussion.Infrastructure;

public static class CassandraInitializer
{
    public static void Initialize(ISession session)
    {
        session.Execute(@"
            CREATE KEYSPACE IF NOT EXISTS distcomp 
            WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};");

        session.ChangeKeyspace("distcomp");


        session.Execute(@"
            CREATE TABLE IF NOT EXISTS tbl_comment (
                issue_id bigint,
                id bigint,
                country text,
                content text,
                state text,  
                PRIMARY KEY (issue_id, id)
            );");

        session.Execute("CREATE INDEX IF NOT EXISTS ON tbl_comment (id);");
    }
}