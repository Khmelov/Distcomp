namespace Discussion.Config
{
    public class CassandraSettings
    {
        public string ContactPoint { get; set; } = "localhost";
        public int Port { get; set; } = 9042;
        public string Keyspace { get; set; } = "distcomp";
    }
}
