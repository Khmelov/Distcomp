using Cassandra;
using Cassandra.Mapping;
using DiscussionService.Models.Entities;
using DiscussionService.Repositories;
using DiscussionService.Services;

var builder = WebApplication.CreateBuilder(args);

builder.WebHost.UseUrls("http://0.0.0.0:24130");

builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();


var cassandra = builder.Configuration.GetSection("CassandraSettings");

var cluster = Cluster.Builder()
    .AddContactPoints(cassandra["ContactPoints:0"])
    .WithPort(int.Parse(cassandra["Port"]!))
    .Build();

var session = await cluster.ConnectAsync(cassandra["Keyspace"]);

// Пересоздаем таблицу с полем state
await session.ExecuteAsync(new SimpleStatement("DROP TABLE IF EXISTS tbl_post"));
await session.ExecuteAsync(new SimpleStatement(
    "CREATE TABLE tbl_post (" +
    "id int PRIMARY KEY, " +
    "news_id int, " +
    "content text, " +
    "state text, " +
    "created timestamp)"
));

MappingConfiguration.Global.Define(
    new Map<Post>()
        .TableName("tbl_post")
        .PartitionKey(p => p.Id)
        .Column(p => p.Id, cm => cm.WithName("id"))
        .Column(p => p.NewsId, cm => cm.WithName("news_id"))
        .Column(p => p.Content, cm => cm.WithName("content"))
        .Column(p => p.State, cm => cm.WithName("state"))
        .Column(p => p.Created, cm => cm.WithName("created"))
);

builder.Services.AddSingleton<Cassandra.ISession>(session);
builder.Services.AddSingleton<IMapper>(_ => new Mapper(session));

builder.Services.AddScoped<IPostRepository, PostRepository>();
builder.Services.AddScoped<IPostService, PostService>();

// Регистрация Kafka
builder.Services.AddSingleton<KafkaProducerService>();
builder.Services.AddHostedService<KafkaConsumerService>();

var app = builder.Build();

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.MapControllers();
app.Run();