using System.Reflection;
using System.Security.Claims;
using System.Text;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using Microsoft.OpenApi;
using Publisher.src.NewsPortal.Publisher.API.Middleware;
using Publisher.src.NewsPortal.Publisher.Application.Services.Abstractions;
using Publisher.src.NewsPortal.Publisher.Application.Services.Implementations;
using Publisher.src.NewsPortal.Publisher.Domain.Entities;
using Publisher.src.NewsPortal.Publisher.Infrastructure.Caching;
using Publisher.src.NewsPortal.Publisher.Infrastructure.Clients.Abstractions;
using Publisher.src.NewsPortal.Publisher.Infrastructure.Clients.Implementations;
using Publisher.src.NewsPortal.Publisher.Infrastructure.Data;
using Publisher.src.NewsPortal.Publisher.Infrastructure.Messaging;
using Publisher.src.NewsPortal.Publisher.Infrastructure.Repositories;
using Publisher.src.NewsPortal.Publisher.Infrastructure.Repositories.Abstractions;
using Publisher.src.NewsPortal.Publisher.Infrastructure.Repositories.Implementations;

var builder = WebApplication.CreateBuilder(args);

var connectionString = builder.Configuration.GetConnectionString("DefaultConnection");

builder.Services.AddDbContext<PostgresDbContext>(options =>
    options.UseNpgsql(connectionString));

builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen(c =>
{
    c.SwaggerDoc("v1.0", new OpenApiInfo
    {
        Title = "NewsPortal Publisher API",
        Version = "v1.0",
        Description = "API for News Portal application"
    });

    var xmlFile = $"{Assembly.GetExecutingAssembly().GetName().Name}.xml";
    var xmlPath = Path.Combine(AppContext.BaseDirectory, xmlFile);
    if (File.Exists(xmlPath))
    {
        c.IncludeXmlComments(xmlPath);
    }
});

builder.Services.AddControllersWithViews();
builder.Services.AddStackExchangeRedisCache(options =>
{
    var redisConfig = builder.Configuration.GetSection("Redis");
    options.Configuration = redisConfig["ConnectionString"];
    options.InstanceName = redisConfig["InstanceName"];
});

var jwtSecret = builder.Configuration["Jwt:Secret"] ?? "your-super-secret-key-with-minimum-64-characters-long-for-jwt";
var key = Encoding.UTF8.GetBytes(jwtSecret);

builder.Services.AddAuthentication(options =>
{
    options.DefaultAuthenticateScheme = JwtBearerDefaults.AuthenticationScheme;
    options.DefaultChallengeScheme = JwtBearerDefaults.AuthenticationScheme;
})
.AddJwtBearer(options =>
{
    options.TokenValidationParameters = new TokenValidationParameters
    {
        ValidateIssuer = false,
        ValidateAudience = false,
        ValidateLifetime = true,
        ValidateIssuerSigningKey = true,

        IssuerSigningKey = new SymmetricSecurityKey(
            Encoding.UTF8.GetBytes(
                builder.Configuration["Jwt:Secret"]
                ?? "super-secret-key-with-minimum-64-characters-long-for-jwt")),

        ClockSkew = TimeSpan.Zero,

        RequireExpirationTime = true,
        RequireSignedTokens = true,

        NameClaimType = ClaimTypes.NameIdentifier,
        RoleClaimType = ClaimTypes.Role
    };
});

builder.Services.AddAuthorization(options =>
{
    options.AddPolicy("AdminOnly", policy => policy.RequireRole("ADMIN"));
    options.AddPolicy("CustomerOnly", policy => policy.RequireRole("CUSTOMER"));
    options.AddPolicy("AdminOrCustomer", policy => policy.RequireRole("ADMIN", "CUSTOMER"));
});

builder.Services.AddScoped<IRepository<Creator>, GenericRepository<Creator>>();
builder.Services.AddScoped<IRepository<News>, NewsRepository>();
builder.Services.AddScoped<IRepository<Mark>, GenericRepository<Mark>>();
builder.Services.AddScoped<IRepository<Note>, GenericRepository<Note>>();

builder.Services.AddHttpClient<IDiscussionApiClient, DiscussionApiClient>(client =>
{
    var discussionUrl = builder.Configuration["DiscussionApi:BaseUrl"] ?? "http://localhost:24130";
    client.BaseAddress = new Uri(discussionUrl);
    client.Timeout = TimeSpan.FromSeconds(30);
    client.DefaultRequestHeaders.Add("Accept", "application/json");
});

var assembly = typeof(Program).Assembly;

assembly.GetTypes()
    .Where(t => t is { IsClass: true, IsAbstract: false } && t.Name.EndsWith("Service"))
    .ToList()
    .ForEach(serviceType =>
    {
        var interfaceType = serviceType.GetInterfaces().FirstOrDefault();
        if (interfaceType != null)
        {
            builder.Services.AddScoped(interfaceType, serviceType);
        }
    });

builder.Services.AddScoped<IJwtService, JwtService>();
builder.Services.AddScoped<ICreatorService, CreatorService>();
builder.Services.AddSingleton<IRedisCacheService, RedisCacheService>();
builder.Services.AddScoped<INewsValidationService, NewsValidationService>();
builder.Services.AddSingleton<IKafkaProducerService, KafkaProducerService>();
builder.Services.AddSingleton<KafkaConsumerService>();

var app = builder.Build();

using (var scope = app.Services.CreateScope())
{
    var dbContext = scope.ServiceProvider.GetRequiredService<PostgresDbContext>();
    try
    {
        //Проверяем, нужно ли применять миграции
        if (dbContext.Database.GetPendingMigrations().Any())
        {
            dbContext.Database.Migrate();
            Console.WriteLine($"Applied migrations: {string.Join(", ", dbContext.Database.GetAppliedMigrations())}");
        }
        else
        {
            Console.WriteLine("Database is up to date. No migrations to apply.");
        }
    }
    catch (Exception ex)
    {
        Console.WriteLine($"Migration error: {ex.Message}");
        //Логируем InnerException для деталей
        if (ex.InnerException != null)
            Console.WriteLine($"Details: {ex.InnerException.Message}");
        throw; //Или продолжаем работу, если БД уже создана
    }
}

app.UseSwagger();
app.UseSwaggerUI(c =>
{
    c.SwaggerEndpoint("/swagger/v1.0/swagger.json", "NewsPortal Publisher API v1.0");
    c.RoutePrefix = string.Empty;
});

app.UseMiddleware<ExceptionHandlingMiddleware>();
app.UseHttpsRedirection();
app.UseStaticFiles();
app.UseRouting();
app.UseAuthentication(); 
app.UseAuthorization();
app.MapControllers();

app.Run();