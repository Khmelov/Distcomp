using Cassandra;

namespace CommentMicroservice.DAO.Implementations;

public class CassandraContext
{
    internal Cassandra.ISession Session {get; init;}
    public CassandraContext()
    {
        Cluster? cluster = Cluster.Builder()
                                .AddContactPoint("127.0.0.1")
                                .WithPort(9042)
                                .Build();
        Session = cluster.Connect("distcomp");
    }
}