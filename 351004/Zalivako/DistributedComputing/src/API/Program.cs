using Application.Interfaces;
using Application.MappingProfiles;
using Application.Services;
using Infrastructure.Kafka;
using Infrastructure.Persistence.EFCore;
using Microsoft.EntityFrameworkCore;

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

            // add postgres
            var connectionString = builder.Configuration.GetConnectionString("DefaultConnection");
            builder.Services.AddDbContext<AppDbContext>(options =>
                options.UseNpgsql(connectionString)
            );

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

            var app = builder.Build();

            // Configure the HTTP request pipeline.

            //app.UseHttpsRedirection();


            app.MapControllers();

            app.Run();
        }
    }
}
