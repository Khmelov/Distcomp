using Domain.Interfaces;
using Domain.Settings;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using MongoDB.Driver;
using Persistence.Repositories;

namespace Persistence.Extensions;

public static class ServiceExtensions
{
    public static IServiceCollection ConfigureMongoDb(this IServiceCollection services, IConfiguration configuration)
    {
        var mongoSettings = configuration.GetSection("MongoDb").Get<MongoDbSettings>();
        if (mongoSettings == null)
        {
            return services;
        }

        var mongoClient = new MongoClient(mongoSettings.ConnectionUrl);
        var database = mongoClient.GetDatabase(mongoSettings.DatabaseName);
        services.AddSingleton(database);
        
        return services;
    }

    public static IServiceCollection ConfigureUnitOfWork(this IServiceCollection services)
    {
        services.AddScoped<IUnitOfWork, UnitOfWork>();

        return services;
    }
}