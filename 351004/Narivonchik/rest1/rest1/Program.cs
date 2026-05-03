using System.Text;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using rest1.application.interfaces;
using rest1.application.interfaces.services;
using rest1.application.mappers;
using rest1.application.services;
using rest1.infrastructure.auth;
using rest1.persistence.db;
using rest1.persistence.db.repositories;
using RedisService.interfaces;
using RedisService.services;
using rest1.application.configs;

var builder = WebApplication.CreateBuilder(args);

// Configure JWT settings
var jwtSettings = builder.Configuration.GetSection(JwtSettings.SectionName);
builder.Services.Configure<JwtSettings>(jwtSettings);
var jwtSecret = jwtSettings["Secret"] ?? throw new InvalidOperationException("JWT Secret not configured");
var key = Encoding.UTF8.GetBytes(jwtSecret);

// Add JWT Authentication
builder.Services.AddAuthentication(options =>
{
    options.DefaultAuthenticateScheme = JwtBearerDefaults.AuthenticationScheme;
    options.DefaultChallengeScheme = JwtBearerDefaults.AuthenticationScheme;
})
.AddJwtBearer(options =>
{
    options.RequireHttpsMetadata = false;
    options.SaveToken = true;
    options.TokenValidationParameters = new TokenValidationParameters
    {
        ValidateIssuerSigningKey = true,
        IssuerSigningKey = new SymmetricSecurityKey(key),
        ValidateIssuer = true,
        ValidIssuer = jwtSettings["Issuer"],
        ValidateAudience = true,
        ValidAudience = jwtSettings["Audience"],
        ValidateLifetime = true,
        ClockSkew = TimeSpan.Zero,
        NameClaimType = System.Security.Claims.ClaimTypes.NameIdentifier,
        RoleClaimType = System.Security.Claims.ClaimTypes.Role
    };
});

builder.Services.AddAuthorization(options =>
{
    options.AddPolicy("AdminOnly", policy => policy.RequireRole("ADMIN"));
    options.AddPolicy("CustomerOnly", policy => policy.RequireRole("CUSTOMER"));
});

// services for swagger ui
builder.Services.AddEndpointsApiExplorer();

// register controllers
builder.Services.AddControllers()
    .AddApplicationPart(typeof(rest1.api.controllers.CreatorsController).Assembly);

// register auto mappers
builder.Services.AddAutoMapper(
    config => {
        config.AddProfile<CreatorProfile>();
        config.AddProfile<NewsProfile>();
        config.AddProfile<MarkProfile>();
        config.AddProfile<NoteProfile>();
    });

// Redis
builder.Services.AddSingleton<IRedisCacheService, RedisCacheService>();

var connectionString = builder.Configuration.GetConnectionString("DefaultConnection");
builder.Services.AddDbContext<RestServiceDbContext>(options =>
    options.UseNpgsql(connectionString)
);

// add discussion service client
var discussionServiceUrl = builder.Configuration["DiscussionService:BaseUrl"];
builder.Services.AddHttpClient("discussion", client =>
{
    client.BaseAddress = new Uri(discussionServiceUrl!);
    client.DefaultRequestHeaders.Add("Accept", "application/json");
});

// register postgres db repositories
builder.Services.AddScoped<INewsRepository, DbNewsRepository>();
builder.Services.AddScoped<ICreatorRepository, DbCreatorRepository>();
builder.Services.AddScoped<IMarkRepository, DbMarkRepository>();
builder.Services.AddScoped<INoteRepository, DbNoteRepository>();

// register services
builder.Services.AddScoped<INewsService, NewsService>();
builder.Services.AddScoped<ICreatorService, CreatorService>();
builder.Services.AddScoped<IMarkService, MarkService>();
builder.Services.AddScoped<INoteService, NoteService>();

// register auth services
builder.Services.AddScoped<IAuthService, AuthService>();
builder.Services.AddScoped<ITokenGenerator, TokenGenerator>();

// Http context accessor for authorization handlers
builder.Services.AddHttpContextAccessor();

var app = builder.Build();

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI(c =>
    {
        c.SwaggerEndpoint("/swagger/v1/swagger.json", "REST API v1.0");
        c.SwaggerEndpoint("/swagger/v2/swagger.json", "REST API v2.0 (Secured)");
    });
}

app.UseAuthentication();
app.UseAuthorization();

app.MapGet("/", () =>
{
    return "API server is running.";
});

app.MapControllers();

app.Run();