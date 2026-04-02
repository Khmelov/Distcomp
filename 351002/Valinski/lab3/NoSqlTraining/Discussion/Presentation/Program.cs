using Presentation.Extensions;
using Presentation.MinimalApi;

var builder = WebApplication.CreateBuilder(args);

builder.AddDependencies();


var app = builder.Build();

if (app.Environment.IsDevelopment())
{
    app.MapOpenApi();
}

app.UseHttpsRedirection();

app.MapControllers();

app.MapGroup("api/v1.0/reactions").MapReactions();

app.Run();
