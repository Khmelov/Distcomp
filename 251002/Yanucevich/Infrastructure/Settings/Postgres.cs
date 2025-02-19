using System;
using FluentMigrator.Runner;
using FluentMigrator.Runner.Processors;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Options;
using Npgsql;
using Npgsql.NameTranslation;

namespace Infrastructure.Settings;

public class Postgres
{
    // private static readonly INpgsqlNameTranslator _translator = new NpgsqlSnakeCaseNameTranslator();

    public static void MapCompositeTypes(){
        Dapper.DefaultTypeMap.MatchNamesWithUnderscores = true;
    }

    public static void AddMigrations(IServiceCollection services){
        services.AddFluentMigratorCore()
            .AddSingleton(s => s.GetRequiredService<IOptionsSnapshot<ProcessorOptions>>().Value)
            .ConfigureRunner(rb => rb.AddPostgres()
                .WithGlobalConnectionString(s => {
                    var config = s.GetRequiredService<IOptions<InfrastructureOptions>>();
                    return config.Value.PostgresConnectionString;
                })  
                .ScanIn(typeof(Postgres).Assembly).For.Migrations())
                .AddLogging(lb => lb.AddFluentMigratorConsole());
    }
}
