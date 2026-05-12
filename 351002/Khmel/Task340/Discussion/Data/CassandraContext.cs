using Cassandra;

namespace Discussion.Data
{
public class CassandraContext
    {
        public Cassandra.ISession Session { get; }

        public CassandraContext()
        {
            var cluster = Cluster.Builder()
                .AddContactPoint("127.0.0.1")
                .WithPort(9042)
                .Build();

            Session = cluster.Connect("distcomp");
        }
    }
}