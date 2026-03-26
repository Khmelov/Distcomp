using Cassandra;
using Microsoft.Extensions.Configuration;

namespace Distcomp.Discussion.Infrastructure.Data
{
    public class CassandraProvider
    {
        public ISession Session { get; }

        public CassandraProvider(IConfiguration config)
        {
            var contactPoint = config.GetValue<string>("Cassandra:ContactPoint") ?? "localhost";
            var port = config.GetValue<int>("Cassandra:Port");
            var keyspace = config.GetValue<string>("Cassandra:Keyspace") ?? "distcomp";

            var cluster = Cluster.Builder()
                .AddContactPoint(contactPoint)
                .WithPort(port)
                .Build();

            Session = cluster.Connect();

            Session.Execute($"CREATE KEYSPACE IF NOT EXISTS {keyspace} WITH replication = {{'class': 'SimpleStrategy', 'replication_factor': 1}};");
            Session.ChangeKeyspace(keyspace);

            Session.Execute(@"
                CREATE TABLE IF NOT EXISTS tbl_note (
                    country text,
                    issue_id bigint,
                    id bigint,
                    content text,
                    PRIMARY KEY ((country), issue_id, id)
                );");
        }
    }
}