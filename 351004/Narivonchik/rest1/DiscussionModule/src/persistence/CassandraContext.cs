using Cassandra;
using Cassandra.Mapping;

namespace DiscussionModule.persistence;

public class CassandraContext
{
    private readonly ICluster _cluster;
    private Cassandra.ISession _session;
    private IMapper _mapper;

    public CassandraContext(IConfiguration configuration)
    {
        var connectionString = configuration.GetConnectionString("CassandraConnection");
        var options = new CassandraOptions(connectionString);
        
        _cluster = Cluster.Builder()
            .AddContactPoint(options.Host)
            .WithPort(options.Port)
            .Build();
        
        // Сначала подключаемся без указания keyspace
        _session = _cluster.Connect();
    }

    public IMapper Mapper => _mapper;
    public Cassandra.ISession Session => _session;

    public void CreateKeyspaceIfNotExists(string keyspaceName)
    {
        var createKeyspaceQuery = $@"
            CREATE KEYSPACE IF NOT EXISTS {keyspaceName} 
            WITH replication = {{ 
                'class': 'SimpleStrategy', 
                'replication_factor': 1 
            }}";
        
        _session.Execute(createKeyspaceQuery);
        
        // Переключаемся на созданный keyspace
        _session.ChangeKeyspace(keyspaceName);
        
        // Создаем mapper после переключения keyspace
        _mapper = new Mapper(_session);
    }

    public void CreateTableIfNotExists()
    {
        var createTableQuery = @"
        CREATE TABLE IF NOT EXISTS tbl_notes (
            id bigint PRIMARY KEY,
            news_id bigint,
            content text,
            country text,
            state text
        )";
    
        _session.Execute(createTableQuery);
    }
    
    public void CreateCounterTableIfNotExists()
    {
        _session.Execute("DROP TABLE IF EXISTS tbl_counters");
        var createCounterTableQuery = @"
        CREATE TABLE IF NOT EXISTS tbl_counters (
            counter_name text PRIMARY KEY,
            counter_value counter 
        )";
    
        _session.Execute(createCounterTableQuery);
    
        var initCounterQuery = @"
        UPDATE tbl_counters 
        SET counter_value = counter_value + 0 
        WHERE counter_name = 'note_id'";
    
        _session.Execute(initCounterQuery);
    }
}

public class CassandraOptions
{
    public string Host { get; }
    public int Port { get; }
    public string Keyspace { get; }

    public CassandraOptions(string connectionString)
    {
        // Парсинг connection string формата: jdbc:cassandra://localhost:9042/distcompcasssandra
        var uri = new Uri(connectionString.Replace("jdbc:", ""));
        Host = uri.Host;
        Port = uri.Port;
        Keyspace = uri.AbsolutePath.TrimStart('/');
    }
}