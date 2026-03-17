using Project.Exceptions;
using Project.Mapper;
using Project.Model;
using Project.Repository;
using Project.Service;
using System.Text.Json;
using System.Text.Json.Serialization;
using Microsoft.OpenApi.Models;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container
builder.Services.AddControllers()
    .AddJsonOptions(options => {
        options.JsonSerializerOptions.PropertyNamingPolicy = JsonNamingPolicy.CamelCase;
        options.JsonSerializerOptions.DefaultIgnoreCondition = JsonIgnoreCondition.WhenWritingNull;
    });

// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen(c => {
    c.SwaggerDoc("v1", new OpenApiInfo { Title = "Tweet API", Version = "v1.0" });
});

// Add AutoMapper
builder.Services.AddAutoMapper(typeof(Program));

// Register repositories
builder.Services.AddSingleton<IRepository<Editor>, InMemoryRepository<Editor>>();
builder.Services.AddSingleton<IRepository<Sticker>, InMemoryRepository<Sticker>>();
builder.Services.AddSingleton<IRepository<Reaction>, InMemoryRepository<Reaction>>();
//builder.Services.AddSingleton<ITweetRepository, TweetRepository>();
builder.Services.AddSingleton<IRepository<Tweet>, InMemoryRepository<Tweet>>();

// Register services
builder.Services.AddScoped<EditorService>();
builder.Services.AddScoped<TweetService>();
builder.Services.AddScoped<StickerService>();
builder.Services.AddScoped<ReactionService>();

// Add exception handler
builder.Services.AddExceptionHandler<GlobalExceptionHandler>();
builder.Services.AddProblemDetails();


var app = builder.Build();

// Configure the HTTP request pipeline
if (app.Environment.IsDevelopment()) {
    app.UseSwagger();
    app.UseSwaggerUI(c => {
        c.SwaggerEndpoint("/swagger/v1/swagger.json", "Tweet API v1.0");
    });
}

app.UseExceptionHandler();
app.UseHttpsRedirection();
app.UseAuthorization();
app.MapControllers();

// Seed initial data
await SeedInitialDataAsync(app.Services);

app.Run();

async Task SeedInitialDataAsync(IServiceProvider serviceProvider) {
    using var scope = serviceProvider.CreateScope();
    var editorRepository = scope.ServiceProvider.GetRequiredService<IRepository<Editor>>();

    // Create first editor as required
    var firstEditor = new Editor {
        Firstname = "Ксения",
        Lastname = "Городко",
        Login = "xgorodko@gmail.com",
        Password = "password123"
    };

    await editorRepository.AddAsync(firstEditor);
    Console.WriteLine("Initial data seeded successfully");
}