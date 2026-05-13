using Cassandra;
using Microsoft.Extensions.Options;

namespace discussion.Infrastructure;

public class CassandraSchemaInitializer
{
    private readonly CassandraOptions _options;
    private readonly ILogger<CassandraSchemaInitializer> _logger;

    public CassandraSchemaInitializer(IOptions<CassandraOptions> options, ILogger<CassandraSchemaInitializer> logger)
    {
        _options = options.Value;
        _logger = logger;
    }

    public void EnsureSchema(ICluster cluster)
    {
        var keyspace = _options.Keyspace;
        using var bootstrap = cluster.Connect();
        bootstrap.Execute(
            $"CREATE KEYSPACE IF NOT EXISTS {keyspace} WITH replication = {{'class': 'SimpleStrategy', 'replication_factor': 1}};");

        var cqlPath = Path.Combine(AppContext.BaseDirectory, "db", "changelog", "cql", "001-notice-tables.cql");
        using var session = cluster.Connect(keyspace);
        if (!File.Exists(cqlPath))
        {
            _logger.LogWarning("Файл {Path} не найден, применяется встроенный DDL", cqlPath);
            ApplyTables(session);
            return;
        }

        var text = File.ReadAllText(cqlPath);
        foreach (var statement in SplitCqlStatements(text))
        {
            if (string.IsNullOrWhiteSpace(statement))
                continue;
            session.Execute(statement);
        }
    }

    public static void ApplyTables(Cassandra.ISession session)
    {
        session.Execute(
            """
            CREATE TABLE IF NOT EXISTS tbl_notice_by_issue (
                issue_id bigint,
                id bigint,
                content text,
                state text,
                PRIMARY KEY ((issue_id), id)
            ) WITH CLUSTERING ORDER BY (id ASC)
            """);

        session.Execute(
            """
            CREATE TABLE IF NOT EXISTS tbl_notice_by_id (
                id bigint PRIMARY KEY,
                issue_id bigint,
                content text,
                state text
            )
            """);

        session.Execute(
            """
            CREATE TABLE IF NOT EXISTS tbl_notice_bucket (
                bucket int,
                id bigint,
                issue_id bigint,
                content text,
                state text,
                PRIMARY KEY ((bucket), id)
            ) WITH CLUSTERING ORDER BY (id ASC)
            """);
    }

    private static IEnumerable<string> SplitCqlStatements(string text)
    {
        var lines = text.Split('\n');
        var buf = lines
            .Where(l => !l.Trim().StartsWith("--", StringComparison.Ordinal))
            .ToList();
        var full = string.Join('\n', buf);
        return full.Split(';', StringSplitOptions.RemoveEmptyEntries | StringSplitOptions.TrimEntries);
    }
}
