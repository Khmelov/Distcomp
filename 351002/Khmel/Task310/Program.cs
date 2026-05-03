var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers(options =>
{
    options.Filters.Add<GlobalExceptionFilter>();
});

builder.Services.AddSingleton<IRepository<Writer>, WriterRepository>();
builder.Services.AddSingleton<IRepository<Story>, StoryRepository>();
builder.Services.AddSingleton<IRepository<Label>, LabelRepository>();
builder.Services.AddSingleton<IRepository<Comment>, CommentRepository>();

builder.Services.AddScoped<IWriterService, WriterService>();
builder.Services.AddScoped<IStoryService, StoryService>();
builder.Services.AddScoped<ILabelService, LabelService>();
builder.Services.AddScoped<ICommentService, CommentService>();

var app = builder.Build();

app.UseHttpsRedirection();
app.MapControllers();
app.Run();

