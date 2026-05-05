using Distcomp.Application.Interfaces;
using Distcomp.Application.Mapping;
using Distcomp.Application.Services;
using Distcomp.Domain.Models;
using Distcomp.Infrastructure.Caching;
using Distcomp.Infrastructure.Data;
using Distcomp.Infrastructure.Messaging;
using Distcomp.Infrastructure.Repositories;
using Distcomp.Shared.Models;
using Distcomp.WebApi.Middleware;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using StackExchange.Redis;
using System.Text;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers();
builder.Services.AddHttpClient();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

builder.Services.AddDbContext<AppDbContext>(options =>
    options.UseNpgsql(builder.Configuration.GetConnectionString("DefaultConnection")));

var redisConnectionString = builder.Configuration.GetSection("Redis:ConnectionString").Value ?? "localhost:6379";
builder.Services.AddSingleton<IConnectionMultiplexer>(ConnectionMultiplexer.Connect(redisConnectionString));

builder.Services.AddControllers()
    .AddJsonOptions(options =>
    {
        options.JsonSerializerOptions.Converters.Add(new System.Text.Json.Serialization.JsonStringEnumConverter());
        options.JsonSerializerOptions.PropertyNameCaseInsensitive = true;
        options.JsonSerializerOptions.PropertyNamingPolicy = System.Text.Json.JsonNamingPolicy.CamelCase;
    });

builder.Services.AddRouting(options => options.LowercaseUrls = true);

builder.Services.AddScoped(typeof(IRepository<>), typeof(EfRepository<>));
//builder.Services.AddSingleton<IRepository<User>, InMemoryRepository<User>>();
//builder.Services.AddSingleton<IRepository<Issue>, InMemoryRepository<Issue>>();
//builder.Services.AddSingleton<IRepository<Marker>, InMemoryRepository<Marker>>();
//builder.Services.AddSingleton<IRepository<Note>, InMemoryRepository<Note>>();

builder.Services.AddScoped<IUserService, UserService>();
builder.Services.AddScoped<IIssueService, IssueService>();
builder.Services.AddScoped<IMarkerService, MarkerService>();

builder.Services.AddSingleton<KafkaProducerService>();
builder.Services.AddSingleton<KafkaRequestReplyService>();
builder.Services.AddHostedService(sp => sp.GetRequiredService<KafkaRequestReplyService>());
builder.Services.AddSingleton<RedisCacheService>();
builder.Services.AddScoped<IAuthService, AuthService>();
builder.Services.AddScoped<AuthService>();

builder.Services.AddHttpClient("DiscussionClient", client =>
{
    client.BaseAddress = new Uri("http://localhost:24130/api/v1.0/");
});

builder.Services.AddScoped<INoteService, NoteRemoteService>(sp =>
{
    var factory = sp.GetRequiredService<IHttpClientFactory>();
    var client = factory.CreateClient("DiscussionClient");
    return new NoteRemoteService(client);
});

builder.Services.AddAutoMapper(typeof(MappingProfile));

builder.Services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
    .AddJwtBearer(options =>
    {
        options.TokenValidationParameters = new TokenValidationParameters
        {
            ValidateIssuer = true,
            ValidateAudience = true,
            ValidateLifetime = true,
            ValidateIssuerSigningKey = true,
            ValidIssuer = builder.Configuration["Jwt:Issuer"],
            ValidAudience = builder.Configuration["Jwt:Audience"],
            IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(builder.Configuration["Jwt:Key"]!))
        };
    });

builder.Services.AddAuthorization();

var app = builder.Build();

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseMiddleware<ExceptionMiddleware>();

app.UseAuthentication(); 
app.UseAuthorization();  

using (var scope = app.Services.CreateScope())
{
    var context = scope.ServiceProvider.GetRequiredService<AppDbContext>();
    //context.Database.EnsureDeleted();
    context.Database.EnsureCreated();

    var userRepo = scope.ServiceProvider.GetRequiredService<IRepository<User>>();

    if (!userRepo.GetAll().Any(u => u.Login == "dipperpryes@mail.ru"))
    {
        userRepo.Create(new User
        {
            Login = "dipperpryes@mail.ru",
            FirstName = "Александр",
            LastName = "Михальков",
            Password = BCrypt.Net.BCrypt.HashPassword("password123"),
            Role = UserRole.ADMIN
        });
    }
}

app.MapControllers();
app.Run();