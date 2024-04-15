using Cassandra;

public class CassandraConnector
{
	private readonly Cluster _cluster;
	private readonly Cassandra.ISession _session;

	public CassandraConnector(string contactPoint, string keyspace)
	{
		_cluster = Cluster.Builder().AddContactPoint(contactPoint).Build();
		_session = _cluster.Connect(keyspace);
	}

	public Cassandra.ISession GetSession()
	{
		return _session;
	}

	public void Dispose()
	{
		_session?.Dispose();
		_cluster?.Dispose();
	}
}
