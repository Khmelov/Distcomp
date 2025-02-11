using Domain.Repository;
using Infrastructure.Repositories.InMemoryRepositories;
using Microsoft.Extensions.DependencyInjection;

namespace Infrastructure;

public static class DependencyInjection
{
    public static void AddInfrastructure(this IServiceCollection services)
    {
        services.AddSingleton<IEditorRepository, InMemoryEditorRepository>();
        services.AddSingleton<IMarkRepository, InMemoryMarkRepository>();
        services.AddSingleton<INewsRepository, InMemoryNewsRepository>();
        services.AddSingleton<IPostRepository, InMemoryPostRepository>();
    }
}