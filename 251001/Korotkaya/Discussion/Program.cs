using Discussion;
using Discussion.Config;
using Discussion.Repositories;
using Discussion.Services;
using System.Text.Json;

var builder = WebApplication.CreateBuilder(args);

var cassandraSettings = builder.Configuration.GetSection("CassandraSettings").Get<CassandraSettings>();

builder.Services.AddSingleton(cassandraSettings);
builder.Services.AddSingleton<IReactionRepository, ReactionRepository>();
builder.Services.AddSingleton<IReactionService, ReactionService>();

builder.Services.AddControllers().AddJsonOptions(opts => {
    opts.JsonSerializerOptions.PropertyNamingPolicy = JsonNamingPolicy.CamelCase;
    opts.JsonSerializerOptions.PropertyNameCaseInsensitive = true;
});


builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

builder.Services.AddSingleton<KafkaReactionConsumerService>();


builder.Services.AddLogging(logging =>
{
    logging.ClearProviders();
    logging.AddConsole();
    logging.SetMinimumLevel(LogLevel.Information);
});

var app = builder.Build();
Console.WriteLine("Host ������� ��������.");

var kafkaConsumer = app.Services.GetRequiredService<KafkaReactionConsumerService>();
var cts = new CancellationTokenSource();
Task.Run(() => kafkaConsumer.RunAsync(cts.Token));

app.UseSwagger();
app.UseSwaggerUI();

app.MapControllers();

app.Urls.Add("http://localhost:24130");
Console.WriteLine("���������� ������� �� http://localhost:24130");

await app.RunAsync();

cts.Cancel();
