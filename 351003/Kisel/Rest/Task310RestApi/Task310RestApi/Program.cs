using Task310RestApi.Interfaces;
using Task310RestApi.Repositories;
using Task310RestApi.Services;
using Task310RestApi.Models;
using Task310RestApi.Mappers;
using Task310RestApi.Middleware;

var builder = WebApplication.CreateBuilder(args);

builder.WebHost.UseUrls("http://localhost:24110");

// 1. Контроллеры + NewtonsoftJson (для корректных имён полей id, login и т.д.)
builder.Services.AddControllers().AddNewtonsoftJson();

// 2. AutoMapper
builder.Services.AddAutoMapper(typeof(AutoMapperProfile));

// 3. Регистрация РЕПОЗИТОРИЕВ (Singleton важен для InMemory!)
builder.Services.AddSingleton<InMemoryCreatorRepository>();
builder.Services.AddSingleton<InMemoryNewsRepository>();
builder.Services.AddSingleton<InMemoryLabelRepository>();
builder.Services.AddSingleton<InMemoryPostRepository>();

// Привязка интерфейсов к репозиториям
builder.Services.AddSingleton<IRepository<Creator>>(sp => sp.GetRequiredService<InMemoryCreatorRepository>());
builder.Services.AddSingleton<IRepository<News>>(sp => sp.GetRequiredService<InMemoryNewsRepository>());
builder.Services.AddSingleton<IRepository<Label>>(sp => sp.GetRequiredService<InMemoryLabelRepository>());
builder.Services.AddSingleton<IRepository<Post>>(sp => sp.GetRequiredService<InMemoryPostRepository>());

// 4. Регистрация СЕРВИСОВ
builder.Services.AddScoped<ICreatorService, CreatorService>();
builder.Services.AddScoped<INewsService, NewsService>();
builder.Services.AddScoped<ILabelService, LabelService>();
builder.Services.AddScoped<IPostService, PostService>();

builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

var app = builder.Build();

// 5. ВАЖНО: Middleware обработки ошибок (чтобы 500 не была пустой)
app.UseMiddleware<ExceptionHandlingMiddleware>();

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseAuthorization();
app.MapControllers();

app.Run();