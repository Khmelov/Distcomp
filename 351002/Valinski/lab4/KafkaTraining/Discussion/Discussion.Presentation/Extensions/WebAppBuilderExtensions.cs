using Cassandra;
using Confluent.Kafka;
using Discussion.Domain.Abstractions;
using Discussion.Infrastructure.Repositories;
using Discussion.Presentation.Consumers;
using Microsoft.Extensions.Options;
using ISession = Cassandra.ISession;

namespace Discussion.Presentation.Extensions;

public static class WebAppBuilderExtensions
{
    public static WebApplicationBuilder AddDependencies(this WebApplicationBuilder builder)
    {
        var cluster = Cluster.Builder().AddContactPoint("localhost").Build();
        var session = cluster.Connect("distcomp");
        
        builder.Services.AddSingleton<ISession>(session);
        builder.Services.AddScoped<IReactionRepository, ReactionRepository>();

        builder.Services.Configure<KafkaConnectionOptions>(builder.Configuration.GetSection("KafkaConnectionOptions"));
        builder.Services.AddSingleton<IProducer<string, string>>(x =>
        {
            var options = x.GetRequiredService<IOptions<KafkaConnectionOptions>>();
            var producerConfig = new ProducerConfig()
            {
                BootstrapServers = options.Value.BootstrapServers,
            };
            
            return new ProducerBuilder<string, string>(producerConfig).Build();
        });
        builder.Services.AddHostedService<KafkaConsumerHandler>();

        builder.Services.AddControllers();
        builder.Services.AddOpenApi();
        return builder;
    }
}
