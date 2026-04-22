using System.Reflection;
using Microsoft.AspNetCore.Mvc.Razor;
using Microsoft.EntityFrameworkCore;
using Microsoft.OpenApi;
using NewsPortal.Data;
using NewsPortal.Data.Repositories;
using NewsPortal.Middleware;
using NewsPortal.Models.Entities;
using NewsPortal.Models.Repositories.Abstractions;

var builder = WebApplication.CreateBuilder(args);

var connectionString = builder.Configuration.GetConnectionString("DefaultConnection");

builder.Services.AddDbContext<AppDbContext>(options =>
    options.UseNpgsql(connectionString));

builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen(c =>
{
    c.SwaggerDoc("v1.0", new OpenApiInfo
    {
        Title = "NewsPortal API",
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
    var dbContext = scope.ServiceProvider.GetRequiredService<AppDbContext>();
    dbContext.Database.EnsureCreated();
}

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI(c =>
    {
        c.SwaggerEndpoint("/swagger/v1.0/swagger.json", "NewsPortal API v1.0");
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