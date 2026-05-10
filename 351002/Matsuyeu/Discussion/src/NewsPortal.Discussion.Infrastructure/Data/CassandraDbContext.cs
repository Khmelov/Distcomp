using Cassandra;
using Cassandra.Mapping;
using Discussion.src.NewsPortal.Discussion.Domain.Entities;
using ISession = Cassandra.ISession;

namespace Discussion.src.NewsPortal.Discussion.Infrastructure.Data
{
    public class CassandraDbContext : IDisposable
    {
        private readonly ICluster _cluster;
        private readonly ISession _session;
        private readonly IMapper _mapper;
        private readonly ILogger<CassandraDbContext> _logger;
        private bool _disposed;

        public CassandraDbContext(IConfiguration configuration, ILogger<CassandraDbContext> logger)
        {
            _logger = logger;

            var contactPoint = configuration["Cassandra:ContactPoint"] ?? "localhost";
            var port = int.Parse(configuration["Cassandra:Port"] ?? "9042");
            var keyspace = configuration["Cassandra:Keyspace"] ?? "discussion_keyspace";
            var replicationFactor = int.Parse(configuration["Cassandra:ReplicationFactor"] ?? "1");

            _logger.LogInformation("Connecting to Cassandra at {ContactPoint}:{Port}", contactPoint, port);

            // Создаем кластер без указания keyspace
            _cluster = Cluster.Builder()
                .AddContactPoint(contactPoint)
                .WithPort(port)
                .Build();

            // Подключаемся без keyspace
            _session = _cluster.Connect();

            // Создаем keyspace если не существует
            var createKeyspaceQuery = $@"
            CREATE KEYSPACE IF NOT EXISTS {keyspace}
            WITH REPLICATION = {{ 
                'class' : 'SimpleStrategy', 
                'replication_factor' : {replicationFactor} 
            }}";

            _session.Execute(createKeyspaceQuery);
            _logger.LogInformation("Keyspace '{Keyspace}' created or already exists", keyspace);

            // Теперь подключаемся к keyspace
            _session.ChangeKeyspace(keyspace);

            // Создаем таблицу
            var createTableQuery = @"
            CREATE TABLE IF NOT EXISTS tbl_note (
                news_id bigint,
                id bigint,
                content text,
                created timestamp,
                PRIMARY KEY (news_id, id)
            )";

            _session.Execute(createTableQuery);
            _logger.LogInformation("Table 'tbl_note' created or already exists");

            // Настраиваем маппинг для Note
            ConfigureMappings();

            // Создаем маппер
            _mapper = new Mapper(_session);
        }

        private void ConfigureMappings()
        {
            // Настройка маппинга для Note
            MappingConfiguration.Global.Define(
                new Map<Note>()
                    .TableName("tbl_note")
                    .PartitionKey(n => n.NewsId)
                    .ClusteringKey(n => n.Id, SortOrder.Descending)
                    .Column(n => n.NewsId, cm => cm.WithName("news_id"))
                    .Column(n => n.Id, cm => cm.WithName("id"))
                    .Column(n => n.Content, cm => cm.WithName("content"))
            );
        }

        public IMapper Mapper => _mapper;
        public ISession Session => _session;

        public async Task<bool> CheckConnectionAsync()
        {
            try
            {
                await _session.ExecuteAsync(new SimpleStatement("SELECT release_version FROM system.local"));
                return true;
            }
            catch (Exception ex)
            {
                _logger?.LogError(ex, "Failed to check Cassandra connection");
                return false;
            }
        }

        public void Dispose()
        {
            if (!_disposed)
            {
                _session?.Dispose();
                _cluster?.Dispose();
                _disposed = true;
            }
        }
    }
}