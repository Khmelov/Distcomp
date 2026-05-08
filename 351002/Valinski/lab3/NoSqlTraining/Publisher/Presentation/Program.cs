using Infrastructure;
using Microsoft.EntityFrameworkCore;
using Presentation.ApiGroups;
using Presentation.Extensions;

var builder = WebApplication.CreateBuilder(args);

builder.AddDependencies();

var app = builder.Build();

using (var scope = app.Services.CreateScope())
{
    var dbContext = scope.ServiceProvider.GetRequiredService<PublisherDbContext>();
    
    var pendingMigrations = dbContext.Database.GetPendingMigrations();
    if (pendingMigrations.Any())
    {
        Console.WriteLine("Applying pending migrations...");
        await dbContext.Database.MigrateAsync();
        Console.WriteLine("Migrations applied successfully.");
    }
    else
    {
        Console.WriteLine("No pending migrations found.");
    }
}

if (app.Environment.IsDevelopment())
{
    app.MapOpenApi();
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseHttpsRedirection();

app.MapControllers();

app.MapGroup("api/v1.0/users").MapUsers();
app.MapGroup("api/v1.0/topics").MapTopics();
app.MapGroup("api/v1.0/labels").MapLabels();
app.MapGroup("api/v1.0/reactions").MapReactions();

app.Run();

