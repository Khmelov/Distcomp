using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using Publisher.Data;
using Publisher.Dtos;
using Publisher.Entities;
using Publisher.Mapper;
using Publisher.Repositories;     
using Publisher.Services;
using System.Text;

var builder = WebApplication.CreateBuilder(args);

// AutoMapper
builder.Services.AddAutoMapper(typeof(MappingProfile));

// Подключение к PostgreSQL
builder.Services.AddDbContext<AppDbContext>(options =>
    options.UseNpgsql(builder.Configuration.GetConnectionString("DefaultConnection")));


// Контроллеры и OpenAPI
builder.Services.AddControllers();
builder.Services.AddOpenApi();

// Сервисы (бизнес-логика)
builder.Services.AddScoped<IService<User, UserRequestTo, UserResponseTo>, UserService>();
builder.Services.AddScoped<IService<Topic, TopicRequestTo, TopicResponseTo>, TopicService>();
builder.Services.AddScoped<IService<Tag, TagRequestTo, TagResponseTo>, TagService>();

//redis
builder.Services.AddStackExchangeRedisCache(options =>
{
    options.Configuration = "localhost:6379";
    options.InstanceName = "Publisher_";
});

builder.Services.AddSingleton<KafkaResponseTracker>();
builder.Services.AddScoped<KafkaReactionRepository>();
//// Регистрация HTTP-клиента для Discussion
//builder.Services.AddHttpClient("DiscussionClient", client =>
//{
//    client.BaseAddress = new Uri("http://localhost:24130");
//    client.DefaultRequestHeaders.Add("Accept", "application/json");
//});
//// 1. Настройка JWT
//var jwtSettings = builder.Configuration.GetSection("Jwt");
//var key = Encoding.ASCII.GetBytes(jwtSettings["Key"]);

////jwt auth
//builder.Services.AddAuthentication(Options => {
//    Options.DefaultAuthenticateScheme = JwtBearerDefaults.AuthenticationScheme;
//    Options.DefaultChallengeScheme = JwtBearerDefaults.AuthenticationScheme;
//})
//.AddJwtBearer(options => {
//    options.TokenValidationParameters = new TokenValidationParameters
//    {
//        ValidateIssuerSigningKey = true,
//        IssuerSigningKey = new SymmetricSecurityKey(key),
//        ValidateIssuer = false,
//        ValidateAudience = false,
//        ClockSkew = TimeSpan.Zero
//    };
//});

//// 2. Настройка Ролей
//builder.Services.AddAuthorization(options => {
//    options.AddPolicy("AdminOnly", policy => policy.RequireRole("ADMIN"));
//    options.AddPolicy("CustomerPolicy", policy => policy.RequireRole("ADMIN", "CUSTOMER"));
//});

//builder.Services.AddScoped<ITokenService, TokenService>();
//builder.Services.AddHttpContextAccessor();
//builder.Services.AddScoped<ITokenService, TokenService>();
//builder.Services.AddScoped<IAuthService, AuthService>();
builder.Services.AddHostedService<OutTopicConsumer>();

// Вместо общего AddScoped(typeof(IRepository<>), typeof(EfRepository<>))
// Регистрируем специфичные версии:
builder.Services.AddScoped<IRepository<User>, UserRepository>();
builder.Services.AddScoped<IRepository<Topic>, TopicRepository>();
builder.Services.AddScoped<IRepository<Tag>, TagRepository>();

// Для всех остальных сущностей оставляем базу:
builder.Services.AddScoped(typeof(IRepository<>), typeof(EfRepository<>));

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