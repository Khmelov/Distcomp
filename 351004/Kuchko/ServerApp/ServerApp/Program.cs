using Microsoft.AspNetCore.Mvc;
using Scalar.AspNetCore;
using ServerApp;
using ServerApp.Infrastructure;
using ServerApp.Models.DTOs;

var builder = WebApplication.CreateBuilder(args);

// 1. Настройка контроллеров с нашим префиксом
builder.Services.AddControllers(options =>
{
    options.Conventions.Add(new ApiPrefixConvention(new RouteAttribute("api/v1.0")));
    options.Filters.Add<GlobalExceptionFilter>();
});

builder.Services.Configure<ApiBehaviorOptions>(options =>
{
    options.InvalidModelStateResponseFactory = context =>
    {
        var errorMsg = string.Join(" | ", context.ModelState.Values
            .SelectMany(v => v.Errors)
            .Select(e => e.ErrorMessage));

        return new BadRequestObjectResult(new ErrorResponse(errorMsg, 40001));
    };
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