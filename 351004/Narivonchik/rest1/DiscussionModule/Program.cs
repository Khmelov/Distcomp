using DiscussionModule.interfaces;
using DiscussionModule.kafka;
using DiscussionModule.mappers;
using DiscussionModule.persistence;
using DiscussionModule.persistence.repositories;
using DiscussionModule.services;
using RedisService.interfaces;  
using RedisService.services;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container
builder.Services.AddControllers();

// Register CassandraContext as singleton
builder.Services.AddSingleton<CassandraContext>(serviceProvider =>
{
    var configuration = serviceProvider.GetRequiredService<IConfiguration>();
    var context = new CassandraContext(configuration);
    
    context.CreateKeyspaceIfNotExists("distcompcasssandra");
    context.CreateTableIfNotExists();
    context.CreateCounterTableIfNotExists();
    
    return context;
});

builder.Services.AddScoped<INoteRepository, NoteRepository>();
builder.Services.AddScoped<INoteService, NoteService>();

// AutoMapper
builder.Services.AddAutoMapper(config =>
{
    config.AddProfile<NoteProfile>();
});

// === KAFKA CONFIGURATION ===
var kafkaEnabled = builder.Configuration.GetValue<bool>("Kafka:Enabled");

if (kafkaEnabled)
{
    var kafkaBootstrapServers = builder.Configuration.GetValue<string>("Kafka:BootstrapServers") ?? "localhost:9092";
    var kafkaInTopic = builder.Configuration.GetValue<string>("Kafka:InTopic") ?? "InTopic";
    var kafkaOutTopic = builder.Configuration.GetValue<string>("Kafka:OutTopic") ?? "OutTopic";

    builder.Services.AddSingleton<KafkaProducer>(provider =>
        new KafkaProducer(kafkaBootstrapServers, kafkaOutTopic));

    builder.Services.AddSingleton<KafkaConsumer>(provider =>
        new KafkaConsumer(
            provider,
            kafkaBootstrapServers,
            kafkaInTopic,
            kafkaOutTopic
        ));

    builder.Services.AddHostedService(provider =>
        provider.GetRequiredService<KafkaConsumer>());
}

// REDIS CONFICURATION
builder.Services.AddSingleton<IRedisCacheService, RedisCacheService>();

var app = builder.Build();

if (kafkaEnabled)
{
    using var scope = app.Services.CreateScope();
    var producer = scope.ServiceProvider.GetRequiredService<KafkaProducer>();
    
    await producer.EnsureTopicsExistAsync(
        builder.Configuration["Kafka:InTopic"],
        builder.Configuration["Kafka:OutTopic"]
    );
}
app.MapControllers();
app.Run();