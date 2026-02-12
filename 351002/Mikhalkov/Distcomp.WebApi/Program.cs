using Distcomp.Application.Interfaces;
using Distcomp.Application.Mapping;
using Distcomp.Application.Services;
using Distcomp.Domain.Models;
using Distcomp.Infrastructure.Repositories;
using Distcomp.WebApi.Middleware;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

builder.Services.AddSingleton<IRepository<User>, InMemoryRepository<User>>();
builder.Services.AddSingleton<IRepository<Issue>, InMemoryRepository<Issue>>();
builder.Services.AddSingleton<IRepository<Marker>, InMemoryRepository<Marker>>();
builder.Services.AddSingleton<IRepository<Note>, InMemoryRepository<Note>>();

builder.Services.AddScoped<IUserService, UserService>();
builder.Services.AddScoped<IIssueService, IssueService>();
builder.Services.AddScoped<IMarkerService, MarkerService>();
builder.Services.AddScoped<INoteService, NoteService>();

builder.Services.AddAutoMapper(typeof(MappingProfile));

var app = builder.Build();

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseMiddleware<ExceptionMiddleware>();

using (var scope = app.Services.CreateScope())
{
    var userRepo = scope.ServiceProvider.GetRequiredService<IRepository<User>>();

    userRepo.Create(new User
    {
        Login = "dipperpryes@mail.ru",
        FirstName = "Александр",
        LastName = "Михальков",
        Password = "password123"
    });
}

app.MapControllers();
app.Run();