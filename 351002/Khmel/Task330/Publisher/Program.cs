using Microsoft.EntityFrameworkCore;
using Publisher.Services;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddDbContext<AppDbContext>(options =>
    options.UseNpgsql(
        "Host=localhost;Port=5432;Database=distcomp;Username=postgres;Password=postgres",
        o => o.MigrationsHistoryTable("__EFMigrationsHistory", "distcomp")
    )
);

builder.Services.AddControllers(options =>
{
    options.Filters.Add<GlobalExceptionFilter>();
});

// Регистрация HTTP-клиента для Discussion
builder.Services.AddHttpClient<IDiscussionClient, DiscussionClient>();

// Остальные сервисы
builder.Services.AddScoped<IRepository<Writer>, WriterRepository>();
builder.Services.AddScoped<IRepository<Story>, StoryRepository>();
builder.Services.AddScoped<IRepository<Label>, LabelRepository>();

builder.Services.AddScoped<IWriterService, WriterService>();
builder.Services.AddScoped<IStoryService, StoryService>();
builder.Services.AddScoped<ILabelService, LabelService>();

var app = builder.Build();

using (var scope = app.Services.CreateScope())
{
    var db = scope.ServiceProvider.GetRequiredService<AppDbContext>();
    db.Database.EnsureCreated();
}

app.MapControllers();
app.Run();