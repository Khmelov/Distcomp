using System.Text.Json.Serialization;
using Discussion.Consumers;
using Discussion.DTO.Request;
using Discussion.DTO.Response;
using Discussion.Extensions;
using Discussion.Middleware;
using Messaging;
using Messaging.Extensions;
using Scalar.AspNetCore;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers()
    .AddJsonOptions(options =>
    {
        options.JsonSerializerOptions.ReferenceHandler = ReferenceHandler.IgnoreCycles;
    });

builder.Services.AddOpenApi();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddCassandra(builder.Configuration)
    .AddServices()
    .AddRepositories()
    .AddInfrastructure()
    .AddKafkaMessageBus()
    .AddKafkaProducer<string, KafkaMessage<MessageResponseDTO>>(options =>
    {
        options.Topic = "OutTopic";
        options.BootstrapServers = Environment.GetEnvironmentVariable("KAFKA_BROKER");
        options.AllowAutoCreateTopics = true;
    })
    .AddKafkaConsumer<string, KafkaMessage<MessageRequestDTO>, InTopicHandler>(options =>
    {
        options.Topic = "InTopic";
        options.AutoOffsetReset = Confluent.Kafka.AutoOffsetReset.Earliest;
        options.EnableAutoOffsetStore = false;
        options.GroupId = "messages-group";
        options.BootstrapServers = Environment.GetEnvironmentVariable("KAFKA_BROKER");
        options.AllowAutoCreateTopics = true;  ///!!!!!!!!!!!!!!
    });

var app = builder.Build();

app.Map("/", () => "Hello, World!");

// Middleware для глобальных ошибок
app.UseMiddleware<GlobalExceptionMiddleware>();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.MapOpenApi();
    app.MapScalarApiReference(options =>
    {
        options.WithTheme(ScalarTheme.DeepSpace);
    });
}

app.UseAuthorization();

app.MapControllers();

app.Run();