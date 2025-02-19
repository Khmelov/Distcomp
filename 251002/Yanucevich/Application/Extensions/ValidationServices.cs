using System;
using Application.Behaviors;
using FluentValidation;
using MediatR;
using Microsoft.Extensions.DependencyInjection;

namespace Application.Extensions;

public static class ValidationServices
{
    public static IServiceCollection AddValidationServices(this IServiceCollection services){
        services.AddScoped(typeof(IPipelineBehavior<,>), typeof(ValidationPipelineBehavior<,>));
        services.AddValidatorsFromAssembly(Application.AssemblyReference.Assembly);

        return services;
    }
}
