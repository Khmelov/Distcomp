using Infrastructure;
using Microsoft.EntityFrameworkCore;
using Presentation.ApiGroups;
using Presentation.Clients;
using Presentation.Profiles;

namespace Presentation.Extensions;

public static class WebAppBuilderExtensions
{
    public static WebApplicationBuilder AddDependencies(this WebApplicationBuilder builder)
    {
        builder.Services.AddDbContext<PublisherDbContext>(options =>
        {
            var connectionString = builder.Configuration.GetValue<string>("PostgresConnection");
            options.UseNpgsql(connectionString);
        });
        
        builder.Services.AddOpenApi();
        builder.Services.AddSwaggerGen();
        builder.Services.AddAutoMapper(cfg =>
        {
            cfg.AddProfile(typeof(UserProfile));
            cfg.AddProfile(typeof(TopicProfile));
            cfg.AddProfile(typeof(LabelProfile));
        });
        builder.Services.AddControllers();
        
        builder.Services.AddHttpClient<ReactionsServiceClient>(client =>
        {
            client.BaseAddress = new Uri("http://localhost:24130/");
            client.Timeout = TimeSpan.FromSeconds(10);
        });
        
        return builder;
    }
    
}
