using System.Text.Json.Serialization;

namespace Shared.Infrastructure.Cassandra;

public class CassandraOptions
{
    public string[] ContactPoints { get; set; } = Array.Empty<string>();
    public int Port { get; set; }
    public string Username { get; set; } = string.Empty;
    public string Password { get; set; } = string.Empty;
    public string Keyspace { get; set; } = string.Empty;
}