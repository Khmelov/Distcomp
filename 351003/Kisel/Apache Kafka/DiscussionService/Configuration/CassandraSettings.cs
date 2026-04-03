namespace DiscussionService.Configuration
{
    public class CassandraSettings
    {
        public string ContactPoints { get; set; } = string.Empty;
        public int Port { get; set; }
        public string Keyspace { get; set; } = string.Empty;
    }
}