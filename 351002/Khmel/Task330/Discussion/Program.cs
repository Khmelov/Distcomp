using Discussion.Repositories;
using Discussion.Services;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers();
builder.Services.AddSingleton<ICommentRepository, CommentRepository>();
builder.Services.AddScoped<ICommentService, CommentService>();

var app = builder.Build();

app.MapControllers();
app.Run();