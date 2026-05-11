namespace RW.Discussion.Caching;

public class RedisSettings
{
    public string ConnectionString { get; set; } = "localhost:6379";
    public string KeyPrefix { get; set; } = "rw";
    public int TtlSeconds { get; set; } = 300;
}
