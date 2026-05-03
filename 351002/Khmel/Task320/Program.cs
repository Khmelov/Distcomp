using Microsoft.EntityFrameworkCore;

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

builder.Services.AddScoped<IRepository<Writer>, WriterRepository>();
builder.Services.AddScoped<IRepository<Story>, StoryRepository>();
builder.Services.AddScoped<IRepository<Label>, LabelRepository>();
builder.Services.AddScoped<IRepository<Comment>, CommentRepository>();

builder.Services.AddScoped<IWriterService, WriterService>();
builder.Services.AddScoped<IStoryService, StoryService>();
builder.Services.AddScoped<ILabelService, LabelService>();
builder.Services.AddScoped<ICommentService, CommentService>();

var app = builder.Build();

using (var scope = app.Services.CreateScope())
{
    var db = scope.ServiceProvider.GetRequiredService<AppDbContext>();
    db.Database.EnsureCreated();
}

app.UseHttpsRedirection();
app.MapControllers();
app.Run();
