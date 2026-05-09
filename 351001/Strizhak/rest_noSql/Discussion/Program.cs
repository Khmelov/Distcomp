using Cassandra;
using Cassandra.Mapping;
using Discussion.Repositories;
using Discussion.Services;
using CassandraSession = Cassandra.ISession;
using ISession = Cassandra.ISession;
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
// Примечание: ключ партиционирования только topic_id. Для равномерного распределения лучше добавить bucket, но для простоты оставим так.

builder.Services.AddSingleton(session);
builder.Services.AddScoped<IMapper>(sp => new Mapper(sp.GetRequiredService<ISession>()));

// Регистрация репозитория и сервиса
builder.Services.AddScoped<IReactionRepository, ReactionRepository>();
builder.Services.AddScoped<IReactionService, ReactionService>();

// AutoMapper (преобразование ReactionEntity <-> DTO)
object value = builder.Services.AddAutoMapper(typeof(Program));

builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

var app = builder.Build();
app.UseSwagger(); app.UseSwaggerUI();
app.MapControllers();
app.Run();