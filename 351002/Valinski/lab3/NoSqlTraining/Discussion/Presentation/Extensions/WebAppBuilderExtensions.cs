using Cassandra;
using Domain.Abstractions;
using Infrastructure.Repositories;
using ISession = Cassandra.ISession;

namespace Presentation.Extensions;

public static class WebAppBuilderExtensions
{
    public static WebApplicationBuilder AddDependencies(this WebApplicationBuilder builder)
    {
        var cluster = Cluster.Builder().AddContactPoint("localhost").Build();
        var session = cluster.Connect("distcomp");
        
        builder.Services.AddSingleton<ISession>(session);
        builder.Services.AddScoped<IReactionRepository, ReactionRepository>();

        builder.Services.AddControllers();
        builder.Services.AddOpenApi();
        
        return builder;
    }
}
