// src/Discussion/NewsPortal.Discussion.API/Program.cs
using System.Reflection;
using Discussion.src.NewsPortal.Discussion.API.Middleware;
using Discussion.src.NewsPortal.Discussion.Infrastructure.Clients.Abstractions;
using Discussion.src.NewsPortal.Discussion.Infrastructure.Clients.Implementations;
using Discussion.src.NewsPortal.Discussion.Infrastructure.Data;
using Discussion.src.NewsPortal.Discussion.Infrastructure.Repositories.Abstractions;
using Discussion.src.NewsPortal.Discussion.Infrastructure.Repositories.Implementations;
using Microsoft.OpenApi;

var builder = WebApplication.CreateBuilder(args);

// Регистрируем CassandraDbContext
builder.Services.AddSingleton<CassandraDbContext>();

// Регистрируем репозитории
builder.Services.AddScoped<INoteRepository, NoteRepository>();

builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen(c =>
{
    c.SwaggerDoc("v1.0", new OpenApiInfo
    {
        Title = "NewsPortal Discussion API",
        Version = "v1.0",
        Description = "API for managing notes in Cassandra database"
    });

    var xmlFile = $"{Assembly.GetExecutingAssembly().GetName().Name}.xml";
    var xmlPath = Path.Combine(AppContext.BaseDirectory, xmlFile);
    if (File.Exists(xmlPath))
    {
        c.IncludeXmlComments(xmlPath);
    }
});

builder.Services.AddControllersWithViews();
builder.Services.AddHttpClient<IPublisherApiClient, PublisherApiClient>(client =>
{
    var publisherUrl = builder.Configuration["PublisherApi:BaseUrl"] ?? "http://localhost:24110";
    client.BaseAddress = new Uri(publisherUrl);
    client.Timeout = TimeSpan.FromSeconds(30);
});
// Автоматическая регистрация всех сервисов (заканчивающихся на "Service")
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

// Проверка подключения к Cassandra при старте
using (var scope = app.Services.CreateScope())
{
    var cassandraContext = scope.ServiceProvider.GetRequiredService<CassandraDbContext>();
    var logger = scope.ServiceProvider.GetRequiredService<ILogger<Program>>();

    try
    {
        var isConnected = await cassandraContext.CheckConnectionAsync();
        if (isConnected)
        {
            Console.WriteLine("✅ Successfully connected to Cassandra");
            logger.LogInformation("Successfully connected to Cassandra");
        }
        else
        {
            Console.WriteLine("❌ Failed to connect to Cassandra");
            logger.LogWarning("Failed to connect to Cassandra");
        }
    }
    catch (Exception ex)
    {
        Console.WriteLine($"Cassandra connection error: {ex.Message}");
        logger.LogError(ex, "Cassandra connection error");
        if (ex.InnerException != null)
            Console.WriteLine($"Details: {ex.InnerException.Message}");
        // Не выбрасываем исключение, так как Cassandra может быть не критична для старта
        // throw; 
    }
}

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI(c =>
    {
        c.SwaggerEndpoint("/swagger/v1.0/swagger.json", "NewsPortal Discussion API v1.0");
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