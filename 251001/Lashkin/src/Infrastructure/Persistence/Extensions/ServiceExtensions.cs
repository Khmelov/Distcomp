using Domain.Repositories;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Options;
using Persistence.Repositories;

namespace Persistence.Extensions;

public static class ServiceExtensions
{
    public static IServiceCollection ConfigureRepositories(this IServiceCollection services)
    {
        services.AddDbContext<RepositoryContext>(options =>
        {
            options.UseInMemoryDatabase(databaseName: "rest");
        });
        services.AddScoped<IUnitOfWork, UnitOfWork>();
        
        return services;
    }
}