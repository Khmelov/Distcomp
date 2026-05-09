using System.Reflection;
using Microsoft.EntityFrameworkCore;
using Microsoft.OpenApi;
using Publisher.src.NewsPortal.Publisher.API.Middleware;
using Publisher.src.NewsPortal.Publisher.Domain.Entities;
using Publisher.src.NewsPortal.Publisher.Infrastructure.Repositories;
using Publisher.src.NewsPortal.Publisher.Infrastructure.Repositories.Abstractions;
using Publisher.src.NewsPortal.Publisher.Infrastructure.Repositories.Implementations;
using Publisher.src.NewsPortal.Publisher.Infrastructure.Data;
using Publisher.src.NewsPortal.Publisher.Infrastructure.Clients.Abstractions;
using Publisher.src.NewsPortal.Publisher.Infrastructure.Clients.Implementations;

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

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI(c =>
    {
        c.SwaggerEndpoint("/swagger/v1.0/swagger.json", "NewsPortal Publisher API v1.0");
        c.RoutePrefix = string.Empty;
    });
}

app.UseMiddleware<ExceptionHandlingMiddleware>();
app.UseHttpsRedirection();
app.UseStaticFiles();
app.UseRouting();
app.UseAuthorization();
app.MapControllers();

app.Run();