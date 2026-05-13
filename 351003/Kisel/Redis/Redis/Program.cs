using Microsoft.EntityFrameworkCore;
using Redis.Data;
using Redis.Services;

var builder = WebApplication.CreateBuilder(args);

// Настройка порта 24110 по условию задачи
builder.WebHost.ConfigureKestrel(options =>
{
    options.ListenLocalhost(24110);
});

builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

// 1. Подключение Postgres
builder.Services.AddDbContext<PublisherDbContext>(options =>
    options.UseNpgsql(builder.Configuration.GetConnectionString("Postgres")));

// 2. Подключение Redis----------------------------------------------------------------------------------------------------------------
builder.Services.AddStackExchangeRedisCache(options =>
{
    options.Configuration = builder.Configuration.GetConnectionString("Redis");
    options.InstanceName = "Publisher_";
});

// 3. Регистрация кастомных сервисов
builder.Services.AddSingleton<ICacheService, RedisCacheService>();
builder.Services.AddSingleton<KafkaService>();

var app = builder.Build();

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseAuthorization();
app.MapControllers();

// Автоматическое создание таблиц в БД при запуске
using (var scope = app.Services.CreateScope())
{
    var db = scope.ServiceProvider.GetRequiredService<PublisherDbContext>();
    db.Database.EnsureCreated();
}

app.Run();