using Discussion.Infrastructure.Migrators;
using Discussion.Presentation.Controllers;

namespace Discussion.Presentation.Middlewares;

public static class WebAppExtensions
{
    public static WebApplication UseDependencies(this WebApplication app)
    {
        app.UseExceptionHandler();
        
        using (var scope = app.Services.CreateScope())
        {
            var runner = scope.ServiceProvider.GetRequiredService<CassandraReactionsMigrator>();
            runner.MigrateAsync().GetAwaiter().GetResult(); 
        }
        
        if (app.Environment.IsDevelopment())
        {
            app.MapOpenApi();
            app.UseSwagger();
            app.UseSwaggerUI();
        }

        app.UseHttpsRedirection();
        app.MapControllers();
        app.MapGroup("api/v1.0/reactions").MapReactionGroup();
        
        return app;
    }
}
