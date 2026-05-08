using Infrastructure.Options;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Options;

namespace Infrastructure;

public static class InfrastructureDiExtension
{
    public static IServiceCollection AddInfrastructure(this IServiceCollection services)
    {
        services.AddDbContext<BlogDbContext>((sp, options) =>
        {
             var connString = sp.GetRequiredService<IOptions<DatabaseOptions>>()
                .Value
                .DbConnectionString;
             
            options.UseNpgsql(connString);
        });
        
        return services;
    }
}
