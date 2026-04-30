using API.Middleware;
using Application.Interfaces;
using Application.MappingProfiles;
using Application.Services;
using Infrastructure.Kafka;
using Infrastructure.Persistence.EFCore;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Caching.Distributed;
using Microsoft.IdentityModel.Tokens;
using System.Text;

namespace API
{
    public class Program
    {
        public static void Main(string[] args)
        {
            var builder = WebApplication.CreateBuilder(args);

            // Add services to the container.
            builder.Services.AddSingleton<IKafkaProducer, KafkaProducer>();

            builder.Services.AddControllers();

            builder.Services.AddAutoMapper(
                config => {
                    config.AddProfile<EditorProfile>();
                    config.AddProfile<NewsProfile>();
                    config.AddProfile<MarkerProfile>();
                    config.AddProfile<PostProfile>();
                });

            // JWT Authentication
            var jwtSection = builder.Configuration.GetSection("Jwt");
            var key = Encoding.UTF8.GetBytes(jwtSection["Key"]);
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
                    ValidateIssuer = true,
                    ValidateAudience = true,
                    ValidateLifetime = true,
                    ValidateIssuerSigningKey = true,
                    ValidIssuer = jwtSection["Issuer"],
                    ValidAudience = jwtSection["Audience"],
                    IssuerSigningKey = new SymmetricSecurityKey(key)
                };
            });

            // add postgres
            var connectionString = builder.Configuration.GetConnectionString("DefaultConnection");
            builder.Services.AddDbContext<AppDbContext>(options =>
                options.UseNpgsql(connectionString)
            );

            // ƒобавление Redis Cache
            builder.Services.AddStackExchangeRedisCache(options =>
            {
                options.Configuration = builder.Configuration.GetValue<string>("Redis:ConnectionString");
                options.InstanceName = "publisher_"; 
            });

            // add microservices
            var discussionUrl = builder.Configuration["DiscussionService:BaseUrl"];
            builder.Services.AddHttpClient("discussion", client =>
            {
                client.BaseAddress = new Uri(discussionUrl!);
            });

            builder.Services.AddScoped<INewsRepository, NewsEfRepository>();
            builder.Services.AddScoped<IEditorRepository, EditorEfRepository>();
            builder.Services.AddScoped<IMarkerRepository, MarkerEfRepository>();
            builder.Services.AddScoped<IPostRepository, PostEfRepository>();

            builder.Services.AddScoped<INewsService, NewsService>();
            builder.Services.AddScoped<IEditorService, EditorService>();
            builder.Services.AddScoped<IMarkerService, MarkerService>();
            builder.Services.AddScoped<IPostService, PostService>();
            
            builder.Services.AddControllers()
                .AddApplicationPart(typeof(API.Controllers.V2.EditorsController).Assembly); // чтобы v2 контроллеры были найдены
            var app = builder.Build();

            // Configure the HTTP request pipeline.

            //app.UseHttpsRedirection();




            app.MapControllers();

            app.Run();
        }
    }
}
