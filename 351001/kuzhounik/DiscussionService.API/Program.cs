using DiscussionService.Application;
using Microsoft.OpenApi.Models;
using Shared.Controllers.Middleware;

var builder = WebApplication.CreateBuilder(args);

// Регистрация приложения
builder.Services.AddApplication<long>(builder.Configuration);

// Регистрация контроллеров
builder.Services.AddControllers();

// Swagger
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen(c =>
{
    c.SwaggerDoc("v1", new OpenApiInfo
    {
        Title = "DiscussionService API",
        Version = "1.0",
        Description = "Микросервис для работы с блогом"
    });
});

var app = builder.Build();

// Настройка Middleware
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI(c =>
    {
        c.SwaggerEndpoint("/swagger/v1/swagger.json", "BlogService API V1");
        c.RoutePrefix = string.Empty;
    });
}

if (app.Environment.IsProduction())
{
    app.UseHttpsRedirection();
    app.UseAuthorization();
}

app.UseMiddleware<HandleErrorMiddleware>();

app.MapControllers();

app.Run();