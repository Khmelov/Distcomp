using Application.Interfaces;
using Application.MappingProfiles;
using Application.Services;
using AutoMapper;
using Infrastructure.Persistence.InMemory;
using System.Security.Cryptography;

namespace API
{
    public class Program
    {
        public static void Main(string[] args)
        {
            var builder = WebApplication.CreateBuilder(args);

            // Add services to the container.

            builder.Services.AddControllers();

            builder.Services.AddAutoMapper(
                config => {
                    config.AddProfile<EditorProfile>();
                    config.AddProfile<NewsProfile>();
                    config.AddProfile<MarkerProfile>();
                    config.AddProfile<PostProfile>();
                });

            builder.Services.AddSingleton<INewsRepository, NewsInMemoryRepository>();
            builder.Services.AddSingleton<IEditorRepository, EditorInMemoryRepository>();
            builder.Services.AddSingleton<IMarkerRepository, MarkerInMemoryRepository>();
            builder.Services.AddSingleton<IPostRepository, PostInMemoryRepository>();

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
