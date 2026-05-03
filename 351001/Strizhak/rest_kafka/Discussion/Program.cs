using Cassandra;
using Cassandra.Mapping;
using Discussion.Repositories;
using Discussion.Services;
using CassandraSession = Cassandra.ISession;

var builder = WebApplication.CreateBuilder(args);

// Cassandra
var contactPoints = builder.Configuration.GetSection("Cassandra:ContactPoints").Get<string[]>();
var port = builder.Configuration.GetValue<int>("Cassandra:Port");
var cluster = Cluster.Builder()
    .AddContactPoints(contactPoints)
    .WithPort(port)
    .Build();
var session = cluster.Connect();

// Создаём keyspace и таблицу (один раз при старте)
session.Execute("CREATE KEYSPACE IF NOT EXISTS distcomp WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };");
session.Execute("USE distcomp;");
session.Execute(@"
    CREATE TABLE IF NOT EXISTS tbl_reaction (
        topic_id bigint,
        id bigint,
        content text,
        PRIMARY KEY (topic_id, id)
    );");

builder.Services.AddSingleton(session);
// ✅ Регистрируем IMapper как синглтон (вместо AddScoped)
builder.Services.AddSingleton<IMapper>(sp => new Mapper(sp.GetRequiredService<CassandraSession>()));

// Регистрация репозитория и сервиса как синглтонов
builder.Services.AddSingleton<IReactionRepository, ReactionRepository>();
builder.Services.AddSingleton<IReactionService, ReactionService>();

//Фоновые сервисы
builder.Services.AddHostedService<InTopicConsumer>();



// AutoMapper (регистрация профилей)
builder.Services.AddAutoMapper(typeof(Program));

builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

var app = builder.Build();


app.UseSwagger();
app.UseSwaggerUI();
app.MapControllers();
app.Run();