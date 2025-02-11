using System.Reflection;
using Application.Validators;
using FluentValidation;
using FluentValidation.AspNetCore;
using Microsoft.Extensions.DependencyInjection;

namespace Application.Extensions;

public static class ServiceExtensions
{
    public static IServiceCollection ConfigureMapper(this IServiceCollection services)
    {
        services.AddAutoMapper(Assembly.GetExecutingAssembly());
        
        return services;
    }

    public static IServiceCollection ConfigureMediatr(this IServiceCollection services)
    {
        services.AddMediatR(configuration => configuration.RegisterServicesFromAssemblies(AppDomain.CurrentDomain.GetAssemblies()));

        return services;
    }

    public static IServiceCollection ConfigureValidation(this IServiceCollection services)
    {
        services.AddFluentValidationAutoValidation();
        services.AddValidatorsFromAssemblyContaining<LabelRequestToValidator>();

        return services;
    }
}