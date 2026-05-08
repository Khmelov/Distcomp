using AutoMapper;
using BusinessLogic.DTO.Request;
using BusinessLogic.DTO.Response;
using BusinessLogic.Profiles;
using BusinessLogic.Repository;
using BusinessLogic.Servicies;
using Cassandra;
using Infrastructure.RepositoryImplementation;
using Infrastructure.ServiceImplementation;
using Infrastructure.Workers;

var builder = WebApplication.CreateBuilder(args);

var cassandraSettings = builder.Configuration.GetSection("Cassandra");
var contactPoints = cassandraSettings.GetSection("ContactPoints").Get<string[]>() ?? new[] { "localhost" };
var cassandraPort = cassandraSettings.GetValue<int>("Port", 9042);
var keyspace = cassandraSettings.GetValue<string>("Keyspace", "distcomp");

Cassandra.ISession session;
try
{
    var cluster = Cluster.Builder()
        .AddContactPoints(contactPoints)
        .WithPort(cassandraPort)
        .Build();

    session = cluster.Connect();
    await CassandraInitializer.InitializeAsync(session, keyspace);
    builder.Services.AddSingleton(session);
}
catch (Exception ex)
{
    Console.WriteLine($"CRITICAL ERROR: Could not connect to Cassandra: {ex.Message}");
    throw;
}

builder.Services.AddScoped(typeof(IRepository<>), typeof(CassandraRepository<>));
builder.Services.AddScoped<IBaseService<PostRequestTo, PostResponseTo>, PostService>();

builder.Services.AddSingleton(provider =>
{
    var config = new MapperConfiguration(
        cfg =>
        {
            cfg.AddProfile<PostProfile>();
        },
        provider.GetService<ILoggerFactory>()
    );

    return config.CreateMapper();
});

builder.Services.AddHostedService<ModerationWorker>();

builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

var app = builder.Build();

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.MapControllers();
app.Run();