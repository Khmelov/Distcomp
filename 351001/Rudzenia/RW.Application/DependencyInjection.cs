using Microsoft.Extensions.DependencyInjection;
using RW.Application.Common;

namespace RW.Application;

public static class DependencyInjection
{
    public static IServiceCollection AddApplication(this IServiceCollection services)
    {
        services.AddMediatR(cfg => cfg.RegisterServicesFromAssembly(typeof(DependencyInjection).Assembly));
        services.AddAutoMapper(typeof(MappingProfile).Assembly);
        return services;
    }
}
