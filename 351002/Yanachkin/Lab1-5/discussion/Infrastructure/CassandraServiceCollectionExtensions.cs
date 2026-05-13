using Cassandra;
using Microsoft.Extensions.Options;

namespace discussion.Infrastructure;

public class CassandraOptions
{
    public const string SectionName = "Cassandra";
    public string ContactPoint { get; set; } = "127.0.0.1";
    public int Port { get; set; } = 9042;
    public string Keyspace { get; set; } = "distcomp";
}

public static class CassandraServiceCollectionExtensions
{
    public static IServiceCollection AddCassandraDiscussion(this IServiceCollection services, IConfiguration configuration)
    {
        services.Configure<CassandraOptions>(configuration.GetSection(CassandraOptions.SectionName));
        services.AddSingleton<CassandraSchemaInitializer>();
        services.AddSingleton<ICluster>(sp =>
        {
            var opt = sp.GetRequiredService<IOptions<CassandraOptions>>().Value;
            return Cluster.Builder()
                .AddContactPoint(opt.ContactPoint)
                .WithPort(opt.Port)
                .Build();
        });
        services.AddSingleton<Cassandra.ISession>(sp =>
        {
            var cluster = sp.GetRequiredService<ICluster>();
            var initializer = sp.GetRequiredService<CassandraSchemaInitializer>();
            initializer.EnsureSchema(cluster);
            var opt = sp.GetRequiredService<IOptions<CassandraOptions>>().Value;
            return cluster.Connect(opt.Keyspace);
        });
        return services;
    }
}
