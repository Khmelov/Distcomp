using System.Text.Json.Serialization;
using Cassandra;
using Cassandra.Mapping;
using Confluent.Kafka;
using Discussion.Application.Services;
using Discussion.Application.Services.Interfaces;
using Discussion.Application.Services.Profiles;
using Discussion.Domain.Interfaces;
using Discussion.Infrastructure;
using Discussion.Infrastructure.Repositories;
using Discussion.Presentation.Background;
using Discussion.Presentation.Middleware;

namespace Discussion.Presentation;

public class Program
{
    public static void Main(string[] args)
    {
        var builder = WebApplication.CreateBuilder(args);

        builder.WebHost.UseUrls("http://localhost:24130");

        builder.Services.AddControllers()
            .AddJsonOptions(options =>
            {
                options.JsonSerializerOptions.Converters.Add(new JsonStringEnumConverter());
            });

        var bootstrapServers = "localhost:9092";


        var workerConsumerConfig = new ConsumerConfig
        {
            BootstrapServers = bootstrapServers,
            GroupId = "discussion-worker-group",
            AutoOffsetReset = AutoOffsetReset.Earliest
        };


        var workerProducerConfig = new ProducerConfig { BootstrapServers = bootstrapServers };

        MappingConfiguration.Global.Define<CassandraMapping>();

        var cluster = Cluster.Builder()
            .AddContactPoint("localhost")
            .WithPort(9042)
            .Build();

        var tmpSession = cluster.Connect();
        CassandraInitializer.Initialize(tmpSession);
        tmpSession.Dispose();


        var session = cluster.Connect("distcomp");
        builder.Services.AddSingleton(session);

        builder.Services.AddSingleton<IConsumer<string, string>>(sp =>
            new ConsumerBuilder<string, string>(workerConsumerConfig).Build());

        builder.Services.AddSingleton<IProducer<string, string>>(sp =>
            new ProducerBuilder<string, string>(workerProducerConfig).Build());

        builder.Services.AddScoped<ICommentRepository, CommentRepository>();
        builder.Services.AddScoped<ICommentService, CommentService>();

        builder.Services.AddHostedService<KafkaWorker>();

        builder.Services.AddAutoMapper(cfg => { cfg.AddProfile<MappingProfile>(); });

        builder.Services.AddControllers();

        var app = builder.Build();

        app.UseMiddleware<ExceptionHandlingMiddleware>();

        app.MapControllers();
        app.Run();
    }
}