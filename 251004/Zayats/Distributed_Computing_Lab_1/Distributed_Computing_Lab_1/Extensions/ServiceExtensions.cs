using Distributed_Computing_Lab_1.Repositories.Implementations;
using Distributed_Computing_Lab_1.Repositories.Interfaces;
using Distributed_Computing_Lab_1.Services.Implementations;
using Distributed_Computing_Lab_1.Services.Interfaces;

namespace Distributed_Computing_Lab_1.Extensions;

public static class ServiceExtensions
{
    public static IServiceCollection AddRepositories(this IServiceCollection services)
    {
        services.AddSingleton<IAuthorRepository, InMemoryAuthorRepository>();
        services.AddSingleton<ITopicRepository, InMemoryTopicRepository>();
        services.AddSingleton<IStickerRepository, InMemoryStickerRepository>();
        services.AddSingleton<IMesssageRepository, InMemoryMessageRepository>();

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
}