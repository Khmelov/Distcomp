using Distributed_Computing_Lab_2.Data;
using Distributed_Computing_Lab_2.Repositories.Implementations;
using Distributed_Computing_Lab_2.Repositories.Interfaces;
using Distributed_Computing_Lab_2.Services.Implementations;
using Distributed_Computing_Lab_2.Services.Interfaces;
using Microsoft.EntityFrameworkCore;

namespace Distributed_Computing_Lab_2.Extensions;

public static class ServiceExtensions
{
    public static IServiceCollection AddRepositories(this IServiceCollection services)
    {
        services.AddScoped<IAuthorRepository, DatabaseAuthorRepository>();
        services.AddScoped<ITopicRepository, DatabaseTopicRepository>();
        services.AddScoped<IStickerRepository, DatabaseStickerRepository>();
        services.AddScoped<IMessageRepository, DatabaseMessageRepository>();

        return services;
    }

    public static IServiceCollection AddServices(this IServiceCollection services)
    {
        services.AddScoped<IAuthorService, AuthorService>();
        services.AddScoped<ITopicService, TopicService>();
        services.AddScoped<IStickerService, StickerService>();
        services.AddScoped<IMessageService, MessageService>();

        return services;
    }

    public static IServiceCollection AddDbContext(this IServiceCollection services, IConfigurationManager config)
    {
        services.AddDbContext<AppDbContext>(options =>
            options.UseNpgsql(config.GetConnectionString("PostgresConnection")));

        return services;
    }
}