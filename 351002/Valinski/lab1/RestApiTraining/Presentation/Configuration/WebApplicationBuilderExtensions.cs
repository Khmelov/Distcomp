using Application.Abstractions;
using Application.Interfaces;
using Application.Profiles;
using Application.Services;
using FluentValidation;
using Infrastructure;
using Infrastructure.Options;
using Infrastructure.Repositories;
using Presentation.Contracts.Requests;
using Presentation.Profiles;

namespace Presentation.Configuration;

public static class WebApplicationBuilderExtensions
{
    public static IServiceCollection AddServices(this IServiceCollection services, IConfiguration configuration)
    {
        services.AddControllers()
            .AddJsonOptions(options => options.JsonSerializerOptions.PropertyNamingPolicy = System.Text.Json.JsonNamingPolicy.CamelCase);
        
        services.AddOpenApi();

        services.AddAutoMapper(typeof(TopicProfile).Assembly,
            typeof(UserDtoProfile).Assembly);

        services.AddScoped<ITopicRepository, TopicRepository>();
        services.AddScoped<IUserRepository, UserRepository>();
        
        services.AddScoped<ITopicService, TopicService>();
        services.AddScoped<IUserService, UserService>();

        services.AddValidatorsFromAssembly(typeof(UserRequestTo).Assembly);
        
        services.Configure<DatabaseOptions>(configuration.GetSection(nameof(DatabaseOptions)));
        
        services.AddInfrastructure();
        return services;
    }
}
