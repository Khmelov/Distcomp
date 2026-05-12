using Cassandra;
using Cassandra.Mapping;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;

namespace Shared.Infrastructure.Cassandra;

public static class CassandraServiceExtensions
{
    public static IServiceCollection AddCassandra(this IServiceCollection services, IConfiguration config)
    {
        var options = config.GetSection("Cassandra").Get<CassandraOptions>();
    
        if (options == null)
        {
            throw new InvalidOperationException("Cassandra configuration section is missing.");
        }

        var cluster = Cluster.Builder()
            .AddContactPoints(options.ContactPoints.AsEnumerable()) 
            .WithPort(options.Port)
            .WithCredentials(options.Username, options.Password)
            .WithLoadBalancingPolicy(new TokenAwarePolicy(new DCAwareRoundRobinPolicy("datacenter1")))
            .Build();

        var session = cluster.Connect(options.Keyspace);

        services.AddSingleton<ICluster>(cluster);
        services.AddSingleton<ISession>(session);
    
        // Правильная регистрация IMapper через фабрику
        services.AddScoped<IMapper>(sp => new Mapper(sp.GetRequiredService<ISession>()));

        return services;
    }
}