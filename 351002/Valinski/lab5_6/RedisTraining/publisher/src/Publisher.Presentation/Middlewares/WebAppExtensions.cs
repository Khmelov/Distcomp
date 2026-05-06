using Microsoft.EntityFrameworkCore;
using Publisher.Infrastructure.DbContexts;
using Publisher.Presentation.Controllers;

namespace Publisher.Presentation.Middlewares;

public static class WebAppExtensions
{
    public static WebApplication UseExtension(this WebApplication app)
    {
        app.UseExceptionHandler();
        if (app.Environment.IsDevelopment())
        {
            app.MapOpenApi();
            app.UseSwagger();
            app.UseSwaggerUI();
        }

        app.UseHttpsRedirection();

        using (var scope = app.Services.CreateScope())
        {
            var db = scope.ServiceProvider.GetRequiredService<PublisherDbContext>();
            var pendingMigrations = db.Database.GetPendingMigrations();
            if (pendingMigrations.Any())
            {
                Console.WriteLine("Applying pending migrations...");
                db.Database.Migrate();
                Console.WriteLine("Migrations applied successfully.");
            }
            else
            {
                Console.WriteLine("No pending migrations found.");
            }
        }

        app.UseAuthentication();
        app.UseAuthorization();
        
        app.MapGroup("api/v1.0/users").MapUserControllerGroup();
        app.MapGroup("api/v1.0/topics").MapTopicControllerGroup();
        app.MapGroup("api/v1.0/labels").MapLabelControllerGroup();
        app.MapGroup("api/v1.0/reactions").MapReactionControllerGroup();
        app.MapGroup("api/v2.0/users").MapAuthControllerGroup();
        app.MapGroup("api/v2.0/login").MapLoginControllerGroup();
        
        return app;
    }
}
