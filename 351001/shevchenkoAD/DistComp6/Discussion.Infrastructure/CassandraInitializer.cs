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
                author_login text, -- Сразу добавляем в определение
                PRIMARY KEY (issue_id, id)
            );");


        TryAddColumn(session, "tbl_comment", "state", "text");
        TryAddColumn(session, "tbl_comment", "author_login", "text");


        session.Execute("CREATE INDEX IF NOT EXISTS ON tbl_comment (id);");
    }

    private static void TryAddColumn(ISession session, string table, string column, string type)
    {
        try
        {
            session.Execute($"ALTER TABLE {table} ADD {column} {type};");
        }
        catch (InvalidQueryException ex) when (ex.Message.Contains("already exists"))
        {
            Console.WriteLine($"[INFO] Column {column} already exists in {table}, skipping...");
        }
    }
}