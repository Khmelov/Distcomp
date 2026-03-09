using Application.Mapper;
using Application.Repository;
using Application.Service;
using Cassandra;
using Dc.Middleware;
using Domain.Models;
using Infrastructe.ApplicationDbContext;
using Infrastructe.ProjectCassandra;
using Infrastructe.Repository.DbPostgresRepository;
using Infrastructe.Repository.LocalRepository;
using Microsoft.EntityFrameworkCore;
using ProjectCassandra;

var builder = WebApplication.CreateBuilder(args);

var cluster = Cluster.Builder()
    .AddContactPoint("127.0.0.1")
    .WithPort(9042)
    .Build();

using (var bootstrapSession = cluster.Connect())
{
    bootstrapSession.Execute(@"
        CREATE KEYSPACE IF NOT EXISTS my_keyspace 
        WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};");
}

var session = cluster.Connect("my_keyspace");


builder.Services.AddSingleton<Cassandra.ISession>(session);


builder.Services.AddDbContext<AppDbContext>(options =>
    options.UseNpgsql(builder.Configuration.GetConnectionString("DefaultConnection")));

builder.Services.AddScoped<IRepository<Comment>, CassandraRepository>();
builder.Services.AddScoped<IService<CommentResponseTo, CommentRequestTo>, CommentService>();

builder.Services.AddScoped<IIssueRepository, DbPostgresIssueRepository>();
builder.Services.AddScoped<IService<IssueResponseTo, IssueRequestTo>, IssueService>();

builder.Services.AddAutoMapper(typeof(MappingProfile).Assembly);

builder.Services.AddControllers();

var app = builder.Build();

using var scope = app.Services.CreateScope();

app.UseMiddleware<HandleErrorMiddleware>();

app.UseHttpsRedirection();
app.MapControllers();

app.Run();