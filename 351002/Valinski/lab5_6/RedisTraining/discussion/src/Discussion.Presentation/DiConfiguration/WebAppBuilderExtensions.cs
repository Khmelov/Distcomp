using Cassandra;
using Confluent.Kafka;
using Discussion.Application.Features.Queries;
using Discussion.Application.Profiles;
using Discussion.Application.Repositories;
using Discussion.Infrastructure.Abstractions;
using Discussion.Infrastructure.Broker.Consumers;
using Discussion.Infrastructure.Migrators;
using Discussion.Infrastructure.Options;
using Discussion.Infrastructure.Repositories;
using Discussion.Presentation.Middlewares;
using Discussion.Presentation.Profiles;
using Discussion.Presentation.Validators;
using FluentValidation;
using MassTransit;
using Microsoft.Extensions.Options;
using Shared.Messages;
using ISession = Cassandra.ISession;

namespace Discussion.Presentation.DiConfiguration;

public static class WebAppBuilderExtensions
{
    public static WebApplicationBuilder AddDependencies(this WebApplicationBuilder builder)
    {
        builder.Services.AddOpenApi();

        // cassandra
        builder.Services.Configure<CassandraOptions>(builder.Configuration.GetSection("CassandraOptions"));
        builder.Services.AddSingleton<ICluster>((sp) =>
        {
            var options = sp.GetRequiredService<IOptions<CassandraOptions>>().Value;
            var cluster = Cluster
                .Builder()
                .AddContactPoints(options.CassandraContactPoints)
                .WithPort(options.Port)
                .Build();

            return cluster;
        });
        builder.Services.AddScoped<ISession>((sp) =>
        {
            var cluster = sp.GetRequiredService<ICluster>();
            var clusterOptions = sp.GetRequiredService<IOptions<CassandraOptions>>().Value;

            var session = cluster.Connect(clusterOptions.Keyspace);
            return session;
        });
        var migrations = typeof(CassandraReactionsMigrator).Assembly.GetTypes()
            .Where(t => typeof(ICassandraMigration).IsAssignableFrom(t) && !t.IsInterface && !t.IsAbstract)
            .ToList();
        foreach (var migration in migrations)
        {
            builder.Services.AddTransient(typeof(ICassandraMigration), migration);
        }

        builder.Services.AddTransient<CassandraReactionsMigrator>();

        // repo
        builder.Services.AddScoped<IReactionRepository, ReactionRepository>();

        // exception handler
        builder.Services.AddExceptionHandler<GlobalExceptionHandler>();
        builder.Services.AddProblemDetails();

        // mappers, profiles
        builder.Services.AddAutoMapper(cfg =>
        {
            cfg.AddProfile<ReactionProfile>();
            cfg.AddProfile<ReactionApplicationProfile>();
        });

        // validators
        builder.Services.AddValidatorsFromAssembly(typeof(ReactionRequestValidator).Assembly);

        // mediatR
        builder.Services.AddMediatR((cfg) => { cfg.RegisterServicesFromAssembly(typeof(GetAllQuery).Assembly); });

        // kafka
        builder.Services.AddMassTransit(x =>
        {
            x.UsingInMemory();

            x.AddRider(rider =>
            {
                rider.AddProducer<ReactionMessage>("OutTopic");

                rider.AddConsumer<ReactionConsumer>();

                rider.UsingKafka((context, k) =>
                {
                    k.Host("localhost:9092");

                    k.TopicEndpoint<ReactionMessage>("InTopic", "reactions-group", e =>
                    {
                        e.AutoOffsetReset = AutoOffsetReset.Latest;
                        e.ConfigureConsumer<ReactionConsumer>(context);
                    });
                });
            });
        });

        builder.Services.AddSwaggerGen();

        // controllers
        builder.Services.AddControllers();

        return builder;
    }
}
