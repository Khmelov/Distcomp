using System.Text;
using BlogService.Application.DependencyInjection;
using BlogService.Infrastructure.PostgreSQL.Context;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.IdentityModel.Tokens;
using Microsoft.OpenApi.Models;
using Shared.Controllers.Middleware;

var builder = WebApplication.CreateBuilder(args);

// Регистрация приложения
builder.Services.AddApplication<long>(builder.Configuration);

// Регистрация контроллеров
builder.Services.AddControllers();

builder.Services.AddHttpClient("CommentsClient", client =>
{
    client.BaseAddress = new Uri("http://localhost:24130/");
});

// Swagger
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen(c =>
{
    c.SwaggerDoc("v1", new OpenApiInfo
    {
        Title = "BlogService API",
        Version = "1.0",
        Description = "Микросервис для работы с блогом"
    });
});

builder.Services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
    .AddJwtBearer(options =>
    {
        options.TokenValidationParameters = new TokenValidationParameters
        {
            ValidateIssuer = false,
            ValidateAudience = false,
            ValidateLifetime = true,
            ValidateIssuerSigningKey = true,
            IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes("YourSuperSecretKeyThatIsAtLeast32BytesLong!")),
            RoleClaimType = "role" // Важно для работы [Authorize(Roles = "ADMIN")]
        };
    });

builder.Services.AddAuthorization();

var app = builder.Build();

using (var scope = app.Services.CreateScope())
{
    var services = scope.ServiceProvider;
    try
    {
        var context = services.GetRequiredService<BlogServiceDbContext>();
        
        // 1. Создаем схему вручную (EF не всегда делает это сам)
        // context.Database.ExecuteSqlRaw("CREATE SCHEMA IF NOT EXISTS distcomp;");
        
        // 2. Создаем таблицы на основе ваших DbSet и OnModelCreating
        // Это создаст tbl_users, tbl_stickers и т.д.
        context.Database.EnsureCreated();
        
        Console.WriteLine("База данных и таблицы успешно проверены/созданы.");
    }
    catch (Exception ex)
    {
        Console.WriteLine($"Ошибка при инициализации БД: {ex.Message}");
    }
}

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
app.UseHttpsRedirection();
app.UseAuthentication();
app.UseAuthorization();
app.MapControllers();
app.Run();