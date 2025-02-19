using System;

namespace Infrastructure.Settings;

public class InfrastructureOptions
{
    // note: this used to be a { get; init; } property, but now
    // IServiceCollection introduces Configure the following way:
    // it gives an instance of InfrastructureOptions and you must
    // change the existing instance. so in that case it cannot be
    // init.
    public required string PostgresConnectionString { get; set; }
}
