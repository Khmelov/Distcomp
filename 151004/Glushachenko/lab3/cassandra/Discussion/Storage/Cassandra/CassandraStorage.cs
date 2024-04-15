using Cassandra;
using Cassandra.Mapping;
using Discussion.PostEntity;
using ISession = Cassandra.ISession;

namespace Discussion.Storage.Cassandra
{
    public class CassandraStorage
    {
        public readonly ISession Session;
        public readonly Cluster Cluster;
        public readonly IMapper Mapper;

        private const string KeyspaceName = "distcomp";
        private const string TableName = "tbl_posts";

        public CassandraStorage()
        {
            Cluster = Cluster.Builder()
                .AddContactPoint("cassandra")
                .WithPort(9042)
                .Build();

            Session = Cluster.Connect();

            var createKeyspaceQuery = $"CREATE KEYSPACE IF NOT EXISTS {KeyspaceName} WITH REPLICATION = " +
                $"{{ 'class' : 'SimpleStrategy', 'replication_factor' : 1 }}";

            Session.Execute(createKeyspaceQuery);
            Session.Execute($"USE {KeyspaceName}");

            var tableCreateQuery = new SimpleStatement(
                $"CREATE TABLE IF NOT EXISTS {TableName} (" +
                "id INT," +
                "tweetId INT," +
                "country TEXT," +
                "content TEXT," +
                "PRIMARY KEY ((country), id, tweetId)" +
                ");"
            );

            Session.Execute(tableCreateQuery);

            Mapper = new Mapper(Session, new MappingConfiguration()
                .Define(new Map<Post>()
                .TableName("tbl_posts")
                .ClusteringKey(u => u.Id)
                .ClusteringKey(u => u.TweetId)
                .PartitionKey(u => u.Country)
                .Column(u => u.Id, c => c.WithName("id"))
                .Column(u => u.TweetId, c => c.WithName("tweetId"))
                .Column(u => u.Country, c => c.WithName("country"))
                .Column(u => u.Content, c => c.WithName("content")))
                );
        }
    }
}