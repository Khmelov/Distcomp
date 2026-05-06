using AutoMapper;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.ApplicationModels;
using Microsoft.AspNetCore.Mvc.Routing;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using publisherApi.Models;
using publisherApi.Services;
using publisherApi.Services.Interfaces;
using RestApiTask.Data;
using RestApiTask.Infrastructure;
using RestApiTask.Mappings;
using RestApiTask.Models;
using RestApiTask.Models.Entities;
using RestApiTask.Repositories;
using RestApiTask.Services;
using RestApiTask.Services.Interfaces;
using StackExchange.Redis;
using System.Text;

namespace RestApiTask;

public class RoutePrefixConvention : IApplicationModelConvention
{
    private readonly AttributeRouteModel _routePrefix;
    public RoutePrefixConvention(IRouteTemplateProvider route) => _routePrefix = new AttributeRouteModel(route);

    public void Apply(ApplicationModel application)
    {
        foreach (var controller in application.Controllers)
        {
            var selectors = controller.Selectors.Where(s => s.AttributeRouteModel != null).ToList();
            if (selectors.Any())
            {
                foreach (var selector in selectors)
                {
                    var template = selector.AttributeRouteModel.Template?.TrimStart('/') ?? string.Empty;

                    // Если контроллер уже явно задаёт маршрут для API версий — пропускаем добавление префикса.
                    // Это позволяет контроллерам с [Route("api/v2.0/...")] оставаться доступными по /api/v2.0/...
                    if (template.StartsWith("api/v2.0", StringComparison.OrdinalIgnoreCase) ||
                        template.StartsWith("api/v1.0", StringComparison.OrdinalIgnoreCase))
                    {
                        continue;
                    }

                    selector.AttributeRouteModel = AttributeRouteModel.CombineAttributeRouteModel(_routePrefix, selector.AttributeRouteModel);
                }
            }
            else
            {
                controller.Selectors.Add(new SelectorModel { AttributeRouteModel = _routePrefix });
            }
        }
    }
}

public class Program
{
    public static void Main(string[] args)
    {
        var builder = WebApplication.CreateBuilder(args);
        builder.WebHost.UseUrls("http://localhost:24110");
        builder.Services.Configure<KafkaSettings>(builder.Configuration.GetSection("Kafka"));

        // Configure JWT Settings
        var jwtSettings = new JwtSettings();
        builder.Configuration.GetSection("Jwt").Bind(jwtSettings);
        builder.Services.Configure<JwtSettings>(builder.Configuration.GetSection("Jwt"));

        builder.Services.AddHttpClient<IMessageService, RemoteMessageService>(client =>
        {
            client.BaseAddress = new Uri("http://localhost:24130/");
        });

        builder.Services.AddDbContext<AppDbContext>(options =>
        {
            options.UseNpgsql(builder.Configuration.GetConnectionString("Postgres"));
        });

        // Configure Redis
        var redisConnectionString = builder.Configuration.GetConnectionString("Redis") ?? "localhost:6379";
        var redisConnection = ConnectionMultiplexer.Connect(redisConnectionString);
        builder.Services.AddSingleton<IConnectionMultiplexer>(redisConnection);
        builder.Services.AddSingleton<ICacheService>(sp => new RedisCacheService(redisConnection));

        builder.Services.AddControllers(options =>
        {
            options.Conventions.Insert(0, new RoutePrefixConvention(new RouteAttribute("api/v1.0")));
        });

        // Add JWT Authentication
        var key = Encoding.ASCII.GetBytes(jwtSettings.SecretKey);
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
                ValidIssuer = jwtSettings.Issuer,
                ValidateAudience = true,
                ValidAudience = jwtSettings.Audience,
                ValidateLifetime = true,
                ClockSkew = TimeSpan.Zero
            };
        });

        // Add Authorization with role-based policies
        builder.Services.AddAuthorization(options =>
        {
            options.AddPolicy("AdminOnly", policy => policy.RequireRole("ADMIN"));
            options.AddPolicy("CustomerOnly", policy => policy.RequireRole("CUSTOMER"));
            options.AddPolicy("AdminOrCustomer", policy => policy.RequireRole("ADMIN", "CUSTOMER"));
        });

        builder.Services.AddScoped<IRepository<Writer>, EfRepository<Writer>>();
        builder.Services.AddScoped<IRepository<Article>, EfRepository<Article>>();
        builder.Services.AddScoped<IRepository<Marker>, EfRepository<Marker>>();
        builder.Services.AddScoped<IRepository<Message>, EfRepository<Message>>();

        builder.Services.AddScoped<IWriterService, WriterService>();
        builder.Services.AddScoped<IArticleService, ArticleService>();
        builder.Services.AddScoped<IMarkerService, MarkerService>();

        // Add Auth Services
        builder.Services.AddScoped<IJwtService, JwtService>();
        builder.Services.AddScoped<IAuthService, AuthService>();

        var configExpression = new MapperConfigurationExpression();
        configExpression.AddProfile<MappingProfile>();
        var mapperConfig = new MapperConfiguration(configExpression);
        IMapper mapper = mapperConfig.CreateMapper();
        builder.Services.AddSingleton(mapper);

        builder.Services.AddEndpointsApiExplorer();
        builder.Services.AddSwaggerGen();

        var app = builder.Build();

        using (var scope = app.Services.CreateScope())
        {
            var db = scope.ServiceProvider.GetRequiredService<AppDbContext>();
            db.Database.EnsureCreated();
            db.Database.ExecuteSqlRaw(
                "CREATE TABLE IF NOT EXISTS tbl_article_marker (" +
                "article_id bigint NOT NULL, " +
                "marker_id bigint NOT NULL, " +
                "CONSTRAINT pk_tbl_article_marker PRIMARY KEY (article_id, marker_id));");
        }

        app.UseMiddleware<ExceptionMiddleware>();

        if (app.Environment.IsDevelopment())
        {
            app.UseSwagger();
            app.UseSwaggerUI();
        }

        // Use Authentication and Authorization middleware
        app.UseAuthentication();
        app.UseAuthorization();

        app.MapControllers();
        app.Run();
    }
}