using System;
using Domain.Repositories;
using Infrastructure.Repositories.InMemory;
using Infrastructure.Repositories.Postgres;
using Infrastructure.Settings;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;

namespace Infrastructure.Extensions;

public static class ServiceCollectionExtensions
{
    public static IServiceCollection AddRepositories(this IServiceCollection services){
        // services.AddSingleton<IMarkerRepository, MarkerRepositoryInMemory>();
        // services.AddSingleton<IUserRepository, UserRepositoryInMemory>();
        // services.AddSingleton<ITweetRepository, TweetRepositoryInMemory>();
        // services.AddSingleton<IReactionRepository, ReactionRepositoryInMemory>();

        services.AddSingleton<IMarkerRepository, MarkerRepositoryPg>();
        services.AddSingleton<IUserRepository, UserRepositoryPg>();
        services.AddSingleton<ITweetRepository, TweetRepositoryPg>();
        services.AddSingleton<IReactionRepository, ReactionRepositoryPg>(); 

        return services;
    }

    public static IServiceCollection AddInfrastructure (
        this IServiceCollection services,
        IConfigurationRoot config
    ){
        
        services.Configure<InfrastructureOptions>(options => {
            options.PostgresConnectionString = config.GetConnectionString("npg");
        });
            
        Postgres.MapCompositeTypes();   
        Postgres.AddMigrations(services);
                
        return services;
    }
}
