using System.Reflection;
using Mapster;
using Microsoft.AspNetCore.Mvc;
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

builder.Services.AddSingleton<IRepository<Author>, InMemoryRepository<Author>>();
builder.Services.AddSingleton<IRepository<Article>, InMemoryRepository<Article>>();
builder.Services.AddSingleton<IRepository<Message>, InMemoryRepository<Message>>();
builder.Services.AddSingleton<IRepository<Sticker>, InMemoryRepository<Sticker>>();

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
        // Просто удаляем все предопределенные сервера
        // Браузер сам подставит текущий адрес (localhost:24110)
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

using (var scope = app.Services.CreateScope())
{
    var authorRepo = scope.ServiceProvider.GetRequiredService<IRepository<Author>>();

    if (!authorRepo.GetAll().Any())
        authorRepo.Create(new Author
        {
            Login = "kuchkomaxim2527@gmail.com",
            Password = "password123",
            Firstname = "Максим",
            Lastname = "Кучко"
        });
}

app.Run();