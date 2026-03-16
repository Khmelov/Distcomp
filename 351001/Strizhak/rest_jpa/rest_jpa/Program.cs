using Microsoft.EntityFrameworkCore;
using rest_api;
using rest_api.Data;
using rest_api.Dtos;
using rest_api.Entities;
using rest_api.InMemory;
using rest_api.Services;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddDbContext<AppDbContext>(options =>
    options.UseNpgsql(builder.Configuration.GetConnectionString("DefaultConnection")));

builder.Services.AddControllers();
// Learn more about configuring OpenAPI at https://aka.ms/aspnet/openapi
builder.Services.AddOpenApi();
builder.Services.AddSingleton<IRepository<User>, UserRepository>();
builder.Services.AddSingleton<IRepository<Topic>, TopicRepository>();
builder.Services.AddSingleton<IRepository<Reaction>, ReactionRepository>();
builder.Services.AddSingleton<IRepository<Tag>, TagRepository>();

// Сервисы
builder.Services.AddScoped<IService<User, UserRequestTo, UserResponseTo>, UserService>();
builder.Services.AddScoped<IService<Topic, TopicRequestTo, TopicResponseTo>, TopicService>();
builder.Services.AddScoped<IService<Reaction, ReactionRequestTo, ReactionResponseTo>, ReactionService>();
builder.Services.AddScoped<IService<Tag, TagRequestTo, TagResponseTo>, TagService>();


var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.MapOpenApi();
}

//app.UseHttpsRedirection();

app.UseAuthorization();

app.MapControllers();

app.Run();
