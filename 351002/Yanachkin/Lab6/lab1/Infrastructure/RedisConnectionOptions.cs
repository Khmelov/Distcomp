namespace lab1.Infrastructure;

public sealed class RedisConnectionOptions
{
    public const string SectionName = "Redis";

    /// <summary>Строка подключения StackExchange.Redis (по умолчанию образ redis с Docker Hub).</summary>
    public string Configuration { get; set; } = "localhost:6379,abortConnect=false";
}
