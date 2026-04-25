using Cassandra;
using Cassandra.Mapping;
using Discussion.Application.Services;
using Discussion.Application.Services.Interfaces;
using Discussion.Application.Services.Profiles;
using Discussion.Domain.Interfaces;
using Discussion.Infrastructure;
using Discussion.Infrastructure.Repositories;
using Discussion.Presentation.Middleware;

namespace Discussion.Presentation;

public class Program
{
    public static void Main(string[] args)
    {
        var builder = WebApplication.CreateBuilder(args);
        
        builder.WebHost.UseUrls("http://localhost:24130");
        
        MappingConfiguration.Global.Define<CassandraMapping>();
        
        var cluster = Cluster.Builder()
            .AddContactPoint("localhost")
            .WithPort(9042)
            .Build();

        var tmpSession = cluster.Connect();
        CassandraInitializer.Initialize(tmpSession); 
        tmpSession.Dispose();


        var session = cluster.Connect("distcomp");
        builder.Services.AddSingleton(session);

        builder.Services.AddScoped<ICommentRepository, CommentRepository>();
        builder.Services.AddScoped<ICommentService, CommentService>();
        
        builder.Services.AddAutoMapper(cfg => { cfg.AddProfile<MappingProfile>(); });

        builder.Services.AddControllers();

        var app = builder.Build();

        app.UseMiddleware<ExceptionHandlingMiddleware>();

        app.MapControllers();
        app.Run();
    }
}