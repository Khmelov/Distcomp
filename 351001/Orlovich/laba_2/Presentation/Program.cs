using Application.Mapper;
using Application.Repository;
using Application.Service;
using Dc.Middleware;
using Domain.Models;
using Infrastructe.ApplicationDbContext;
using Infrastructe.Repository.DbPostgresRepository;
using Infrastructe.Repository.LocalRepository;
using Microsoft.EntityFrameworkCore;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddDbContext<AppDbContext>(options =>
    options.UseNpgsql(builder.Configuration.GetConnectionString("DefaultConnection")));

builder.Services.AddScoped<IAuthorRepository, DbPostgresAuthorRepository>();
builder.Services.AddScoped<IService<AuthorResponseTo,AuthorRequestTo>,AuthorService>();

builder.Services.AddScoped<IRepository<Comment>, DbPostgresCommentRepository>();
builder.Services.AddScoped<IService<CommentResponseTo,CommentRequestTo>,CommentService>();

builder.Services.AddScoped<IRepository<Marker>, DbPostgresMarkerRepository>();
builder.Services.AddScoped<IService<MarkerResponseTo,MarkerRequestTo>,MarkerService>();

builder.Services.AddScoped<IIssueRepository, DbPostgresIssueRepository>();
builder.Services.AddScoped<IService<IssueResponseTo,IssueRequestTo>,IssueService>();

builder.Services.AddAutoMapper(typeof(MappingProfile).Assembly);

builder.Services.AddControllers();
builder.Services.AddOpenApi();

var app = builder.Build();

using var scope = app.Services.CreateScope();
var db = scope.ServiceProvider.GetRequiredService<AppDbContext>();

var retries = 10;
while (retries-- > 0)
{
    try
    {
        db.Database.Migrate();
        Console.WriteLine("Database migrated");
        break;
    }
    catch(Exception ex)
    {
        Console.WriteLine($"Error message: {ex.Message}");
        Console.WriteLine("Postgres not ready, retrying...");
        Thread.Sleep(3000);
    }
}

app.UseMiddleware<HandleErrorMiddleware>(); 

app.UseHttpsRedirection();
app.MapControllers();

app.Run();