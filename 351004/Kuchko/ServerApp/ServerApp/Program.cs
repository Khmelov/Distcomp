using Microsoft.AspNetCore.Mvc;
using Scalar.AspNetCore;
using ServerApp;
using ServerApp.Infrastructure;

var builder = WebApplication.CreateBuilder(args);

// 1. Настройка контроллеров с нашим префиксом
builder.Services.AddControllers(options =>
{
    options.Conventions.Add(new ApiPrefixConvention(new RouteAttribute("api/v1.0")));
});

builder.Services.AddOpenApi(); 

var app = builder.Build();

if (app.Environment.IsDevelopment())
{
    // Генерирует эндпоинт с JSON описанием API (по умолчанию /openapi/v1.json)
    app.MapOpenApi(); 
    
    app.MapScalarApiReference(options =>
    {
        options
            .WithTitle("My Project API v1.0")
            .WithTheme(ScalarTheme.DeepSpace);
    });
}


// app.UseHttpsRedirection();
app.UseAuthorization();
app.MapControllers();
app.Run();