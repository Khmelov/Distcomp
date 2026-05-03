using Microsoft.EntityFrameworkCore;
using Publisher.Data;
using Publisher.Dtos;
using Publisher.Entities;
using Publisher.Mapper;
using Publisher.Repositories;     
using Publisher.Services;

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
builder.Services.AddScoped<IService<Tag, TagRequestTo, TagResponseTo>, TagService>();


builder.Services.AddSingleton<KafkaResponseTracker>();
builder.Services.AddScoped<KafkaReactionRepository>();
//// Регистрация HTTP-клиента для Discussion
//builder.Services.AddHttpClient("DiscussionClient", client =>
//{
//    client.BaseAddress = new Uri("http://localhost:24130");
//    client.DefaultRequestHeaders.Add("Accept", "application/json");
//});

builder.Services.AddHostedService<OutTopicConsumer>();
// Регистрация прокси


var app = builder.Build();

using (var scope = app.Services.CreateScope())
{
    var context = scope.ServiceProvider.GetRequiredService<AppDbContext>();
    context.Database.Migrate(); 

}

if (app.Environment.IsDevelopment())
{
    app.MapOpenApi();
}

app.UseAuthorization();
app.MapControllers();
app.Run();