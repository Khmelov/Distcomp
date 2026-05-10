using Cassandra;
using RW.Discussion.Middleware;
using RW.Discussion.Services;
using ISession = Cassandra.ISession;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers();
builder.Services.AddSwaggerGen();

var cassandraConfig = builder.Configuration.GetSection("Cassandra");
var contactPoint = cassandraConfig["ContactPoint"] ?? "localhost";
var port = int.Parse(cassandraConfig["Port"] ?? "9042");
var keyspace = cassandraConfig["Keyspace"] ?? "distcomp";

var cluster = Cluster.Builder()
    .AddContactPoint(contactPoint)
    .WithPort(port)
    .Build();

ISession session = null!;
for (var attempt = 1; attempt <= 30; attempt++)
{
    try
    {
        session = cluster.Connect();
        Console.WriteLine("Connected to Cassandra.");
        break;
    }
    catch (Exception ex)
    {
        Console.WriteLine($"Cassandra not ready (attempt {attempt}/30): {ex.Message}");
        if (attempt == 30) throw;
        Thread.Sleep(3000);
    }
}

session.Execute(
    "CREATE KEYSPACE IF NOT EXISTS " + keyspace +
    " WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}");

session.ChangeKeyspace(keyspace);

session.Execute(@"
    CREATE TABLE IF NOT EXISTS tbl_note (
        id bigint,
        article_id bigint,
        content text,
        first_name text,
        last_name text,
        PRIMARY KEY (id)
    )");

builder.Services.AddSingleton<ISession>(session);
builder.Services.AddSingleton<INoteService, CassandraNoteService>();

var app = builder.Build();

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseMiddleware<ExceptionHandlingMiddleware>();
app.MapControllers();

app.Run();
