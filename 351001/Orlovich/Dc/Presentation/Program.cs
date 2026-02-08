using Application.Mapper;
using Application.Repository;
using Application.Service;
using Dc.Middleware;
using Domain.Models;
using Infrastructe.Repository;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddSingleton<IRepository<Editor>,LocalEditorRepository>();
builder.Services.AddScoped<IService,Service>();

builder.Services.AddAutoMapper(typeof(MappingProfile).Assembly);

builder.Services.AddControllers();
builder.Services.AddOpenApi();

var app = builder.Build();


app.UseMiddleware<HandleErrorMiddleware>(); 

app.UseHttpsRedirection();
app.MapControllers();

app.Run();