using Cassandra;
using Discussion.Infrastructure.Abstractions;
using Microsoft.Extensions.Logging;

namespace Discussion.Infrastructure.Migrators;

public class CassandraReactionsMigrator
{
    private readonly ISession _session;
    private readonly IEnumerable<ICassandraMigration> _migrations;
    private readonly ILogger<CassandraReactionsMigrator> _logger;

    public CassandraReactionsMigrator(ISession session, IEnumerable<ICassandraMigration> migrations, ILogger<CassandraReactionsMigrator> logger)
    {
        _session = session;
        _migrations = migrations;
        _logger = logger;
    }

    public async Task MigrateAsync()
    {
        var createMigrationsHistoryTableCql = new SimpleStatement(@"
            CREATE TABLE IF NOT EXISTS migrations_history(
                id text PRIMARY KEY,
                applied_at timestamp)");

        await _session.ExecuteAsync(createMigrationsHistoryTableCql);

        var set = new HashSet<string>();

        var rs = await _session.ExecuteAsync(new SimpleStatement("SELECT id FROM migrations_history"));
        foreach (var row in rs)
        {
            set.Add(row.GetValue<string>("id"));
        }

        var pendingMigrations = _migrations
            .Where(m => !set.Contains(m.Id))
            .OrderBy(m => m.Id) 
            .ToList();

        foreach (var migration in pendingMigrations)
        {
            try
            {
                _logger.LogInformation("Migrating {MigrationId}", migration.Id);
                await migration.UpAsync(_session);
                var insertMigrationStmt =
                    new SimpleStatement(
                        "INSERT INTO migrations_history (id, applied_at) VALUES (?, toTimestamp(now()))", migration.Id);
                await _session.ExecuteAsync(insertMigrationStmt);
            }
            catch (Exception e)
            {
                _logger.LogCritical(e, "Failed to migrate {MigrationId}", migration.Id);
                throw;
            }
        }
    }
}
