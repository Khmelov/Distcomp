using Microsoft.EntityFrameworkCore;
using rest_api;
using rest_api.Data;
using rest_api.Dtos;
using rest_api.Entities;
using rest_api.Mapper;
using rest_api.Repositories;      // пространство имён для EfRepository<>
using rest_api.Services;

var builder = WebApplication.CreateBuilder(args);

// AutoMapper
builder.Services.AddAutoMapper(typeof(MappingProfile));

// Подключение к PostgreSQL
builder.Services.AddDbContext<AppDbContext>(options =>
    options.UseNpgsql(builder.Configuration.GetConnectionString("DefaultConnection")));

// Регистрация обобщённого EF-репозитория
builder.Services.AddScoped(typeof(IRepository<>), typeof(EfRepository<>));

// Контроллеры и OpenAPI
builder.Services.AddControllers();
builder.Services.AddOpenApi();

// Сервисы (бизнес-логика)
builder.Services.AddScoped<IService<User, UserRequestTo, UserResponseTo>, UserService>();
builder.Services.AddScoped<IService<Topic, TopicRequestTo, TopicResponseTo>, TopicService>();
builder.Services.AddScoped<IService<Reaction, ReactionRequestTo, ReactionResponseTo>, ReactionService>();
builder.Services.AddScoped<IService<Tag, TagRequestTo, TagResponseTo>, TagService>();

var app = builder.Build();

using (var scope = app.Services.CreateScope())
{
    var context = scope.ServiceProvider.GetRequiredService<AppDbContext>();
    context.Database.Migrate(); // применяем миграции, если их нет

    // Проверяем, есть ли пользователь с Id = 1
    if (!context.Users.Any(u => u.Id == 1))
    {
        context.Users.Add(new User
        {
            Id = 1,
            Login = "veranikastryzhak@gmail.com",
            Password = BCrypt.Net.BCrypt.HashPassword("password123"),
            Firstname = "Veranika",
            Lastname = "Stryzhak"
        });
        context.SaveChanges();
    }
}

if (app.Environment.IsDevelopment())
{
    app.MapOpenApi();
}

app.UseAuthorization();
app.MapControllers();
app.Run();