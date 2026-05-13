namespace Discussion.Infrastructure.Options;

public class CassandraOptions
{
    public required string[] CassandraContactPoints { get; set; }
    public required string Username { get; set; }
    public required string Password { get; set; }
    public required string Keyspace { get; set; }
    public int Port { get; set; }
}
