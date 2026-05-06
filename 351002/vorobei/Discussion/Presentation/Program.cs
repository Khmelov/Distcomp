using AutoMapper;
using BusinessLogic.DTO.Request;
using BusinessLogic.DTO.Response;
using BusinessLogic.Repository;
using BusinessLogic.Servicies;
using Cassandra;
using Infrastructure.RepositoryImplementation;
using Infrastructure.ServiceImplementation;
using BusinessLogic.Profiles;

var builder = WebApplication.CreateBuilder(args);

var cluster = Cluster.Builder()
    .AddContactPoints("localhost")
    .WithPort(9042)
    .Build();

var session = cluster.Connect(); 
await CassandraInitializer.InitializeAsync(session, "distcomp");

builder.Services.AddSingleton(session);

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