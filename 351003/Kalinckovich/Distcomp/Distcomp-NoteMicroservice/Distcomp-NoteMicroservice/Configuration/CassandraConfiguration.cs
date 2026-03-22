namespace Distcomp_NoteMicroservice.Configuration;


public class CassandraConfig
{
    public const string SectionName = "Cassandra";

    public string[] ContactPoints { get; set; } = ["127.0.0.1"];
    public int Port { get; set; } = 9042;
    public string Keyspace { get; set; } = string.Empty;
    public string? Username { get; set; }
    public string? Password { get; set; }
    public bool UseSsl { get; set; } = false;
}