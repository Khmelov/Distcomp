namespace Distcomp_NoteMicroservice.Infrastructure;

using Cassandra;
using Distcomp_NoteMicroservice.Configuration;
using Microsoft.Extensions.Options;


public static class CassandraServiceExtensions
{
    public static IServiceCollection AddCassandra(
        this IServiceCollection services,
        IConfiguration configuration)
    {
        
        services.Configure<CassandraConfig>(configuration.GetSection(CassandraConfig.SectionName));

        
        services.AddSingleton<ICluster>(sp =>
        {
            var config = sp.GetRequiredService<IOptions<CassandraConfig>>().Value;
            
            var queryOptions = new QueryOptions()
                .SetConsistencyLevel(ConsistencyLevel.LocalQuorum)
                .SetSerialConsistencyLevel(ConsistencyLevel.LocalSerial);

            var clusterBuilder = Cluster.Builder()
                .AddContactPoints(config.ContactPoints)
                .WithPort(config.Port)
                .WithQueryOptions(queryOptions)
                .WithSocketOptions(new SocketOptions()
                    .SetConnectTimeoutMillis(5000)
                    .SetReadTimeoutMillis(10000))
                .WithPoolingOptions(new PoolingOptions()
                    .SetMaxConnectionsPerHost(HostDistance.Local, 8)
                    .SetMaxConnectionsPerHost(HostDistance.Remote, 2))
                .WithReconnectionPolicy(new ExponentialReconnectionPolicy(1000, 60000))
                .WithRetryPolicy(new DefaultRetryPolicy());

            if (!string.IsNullOrEmpty(config.Username) && !string.IsNullOrEmpty(config.Password))
            {
                clusterBuilder = clusterBuilder.WithCredentials(config.Username, config.Password);
            }

            if (config.UseSsl)
            {
                clusterBuilder = clusterBuilder.WithSSL();
            }

            return clusterBuilder.Build();
        });

        
        services.AddSingleton<ISession>(sp =>
        {
            ICluster cluster = sp.GetRequiredService<ICluster>();
            
            CassandraConfig config = sp.GetRequiredService<IOptions<CassandraConfig>>().Value;
            
            return cluster.Connect(config.Keyspace);
        });

        return services;
    }
}