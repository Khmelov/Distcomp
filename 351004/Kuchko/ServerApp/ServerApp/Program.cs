using System.Reflection;
using Mapster;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Scalar.AspNetCore;
using ServerApp.Infrastructure;
using ServerApp.Models.DTOs;
using ServerApp.Models.Entities;
using ServerApp.Repository;
using ServerApp.Services.Implementations;
using ServerApp.Services.Interfaces;

var builder = WebApplication.CreateBuilder(args);

var config = TypeAdapterConfig.GlobalSettings;
config.Scan(Assembly.GetExecutingAssembly());

var connectionString = builder.Configuration.GetConnectionString("DefaultConnection");
builder.Services.AddDbContext<AppDbContext>(options =>
    options.UseNpgsql(connectionString));

builder.Services.AddScoped(typeof(IRepository<>), typeof(EfRepository<>)); 
builder.Services.AddScoped<IAuthorService, AuthorService>();
builder.Services.AddScoped<IArticleService, ArticleService>();
builder.Services.AddScoped<IMessageService, MessageService>();
builder.Services.AddScoped<IStickerService, StickerService>();

builder.Services.AddControllers(options =>
    {
        options.Conventions.Add(new ApiPrefixConvention(new RouteAttribute("api/v1.0")));
        options.Filters.Add<GlobalExceptionFilter>();
    })
    .ConfigureApiBehaviorOptions(options =>
    {
        options.InvalidModelStateResponseFactory = context =>
        {
            var errorMsg = string.Join(" | ", context.ModelState.Values
                .SelectMany(v => v.Errors)
                .Select(e => e.ErrorMessage));

            return new BadRequestObjectResult(new ErrorResponse(errorMsg, 40001));
        };
    });

builder.Services.AddOpenApi(options =>
{
    options.AddDocumentTransformer((document, context, cancellationToken) =>
    {
        document.Servers.Clear();
        return Task.CompletedTask;
    });
});

builder.Services.AddCors(options =>
{
    options.AddDefaultPolicy(policy =>
    {
        policy.AllowAnyOrigin()
            .AllowAnyMethod()
            .AllowAnyHeader();
    });
});

var app = builder.Build();
app.UseCors();

if (app.Environment.IsDevelopment())
{
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

using (var scope = app.Services.CreateScope())
{
    var context = scope.ServiceProvider.GetRequiredService<AppDbContext>();

    try
    {
        // 1. Применяет все не примененные миграции (создает таблицы tbl_...)
        context.Database.Migrate();

        // 2. Добавляет начальную запись по ТЗ, если база пустая
        if (!context.Authors.Any())
        {
            context.Authors.Add(new Author
            {
                Login = "kuchkomaxim2527@gmail.com",
                Password = "password123",
                Firstname = "Максим",
                Lastname = "Кучко"
            });
            context.SaveChanges();
        }
    }
    catch (Exception ex)
    {
        // Выведет ошибку в консоль Docker, если БД недоступна
        Console.WriteLine($"Ошибка при инициализации БД: {ex.Message}");
    }
}

app.Run();