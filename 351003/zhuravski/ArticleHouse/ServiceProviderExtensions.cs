using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using Additions.Cache.Implementations;
using Additions.Cache.Interfaces;
using Additions.Messaging.Implementations;
using Additions.Messaging.Interfaces;
using ArticleHouse.DAO.Implementations;
using ArticleHouse.DAO.Interfaces;
using ArticleHouse.DAO.Models;
using ArticleHouse.Service.Implementations;
using ArticleHouse.Service.Interfaces;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using StackExchange.Redis;

namespace ArticleHouse;

static internal class ServiceProviderExtensions
{
    public static IServiceCollection AddArticleHouseServices(this IServiceCollection collection, ConfigurationManager configuration)
    {
        collection.AddScoped<ICreatorService, CreatorService>();
        collection.AddScoped<ICreatorDAO, DbCreatorDAO>();

        collection.AddScoped<IArticleService, ArticleService>();
        collection.AddScoped<IArticleDAO, DbArticleDAO>();

        collection.AddScoped<ICommentService, CommentService>();

        collection.AddScoped<IMarkService, MarkService>();
        collection.AddScoped<IMarkDAO, DbMarkDAO>();

        collection.AddScoped<IAuthService, AuthService>();

        collection.AddAuthentication(options =>
        {
            options.DefaultAuthenticateScheme = JwtBearerDefaults.AuthenticationScheme;
            options.DefaultChallengeScheme = JwtBearerDefaults.AuthenticationScheme;
        })
        .AddJwtBearer(options =>
        {
            options.TokenValidationParameters = new TokenValidationParameters
            {
                ValidateIssuer = true,
                ValidateAudience = true,
                ValidateLifetime = true,
                ValidateIssuerSigningKey = true,
                ValidIssuer = configuration["Jwt:Issuer"],
                ValidAudience = configuration["Jwt:Audience"],
                IssuerSigningKey = new SymmetricSecurityKey(
                    Encoding.UTF8.GetBytes(configuration["Jwt:Key"]!)),
                NameClaimType = JwtRegisteredClaimNames.Sub,
                RoleClaimType = ClaimTypes.Role
            };
        });

        collection.AddAuthorizationBuilder()
            .AddPolicy("AdminOnly", policy => policy.RequireRole(CreatorModel.ADMIN_ROLE))
            .AddPolicy("CustomerOrAdmin", policy => policy.RequireRole(CreatorModel.CUSTOMER_ROLE, CreatorModel.ADMIN_ROLE));

        collection.AddScoped<IArticleMarkDAO, DbArticleMarkDAO>();

        collection.AddSingleton<IEventOrchestrator, EventOrchestrator>();
        collection.AddSingleton<IEventProducer, KafkaProducer>();
        collection.AddHostedService<KafkaConsumer>();

        collection.AddSingleton<IConnectionMultiplexer>(sp =>
            ConnectionMultiplexer.Connect(configuration["Redis:ConnectionString"]!));
        collection.AddSingleton<IDistributedCache, RedisDistributedCache>();

        var connection = configuration.GetConnectionString("DefaultConnection");
        collection.AddDbContext<ApplicationContext>(options => options.UseNpgsql(connection).UseSnakeCaseNamingConvention());
        return collection;
    }
}