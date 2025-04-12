using Microsoft.AspNetCore.Builder;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.AspNetCore.Hosting;
using Microsoft.Extensions.Hosting;
using System;
using System.Text.Json;
using Npgsql;
using Microsoft.EntityFrameworkCore;

namespace MyApp
{
    public class Program
    {
        public static void Main(string[] args)
        {
            var builder = WebApplication.CreateBuilder(args);

            builder.Services.AddDbContext<AppDbContext>(options =>
                options.UseNpgsql(builder.Configuration.GetConnectionString("DefaultConnection")));

            builder.Services.AddScoped(typeof(IGenericRepository<>), typeof(PostgresRepository<>));

            builder.Services.AddScoped<IEditorService, EditorService>();
            builder.Services.AddScoped<ILabelService, LabelService>();
            builder.Services.AddScoped<INoteService, NoteService>();
            builder.Services.AddScoped<IStoryService, StoryService>();

            /*builder.Services.AddSingleton<IGenericRepository<Editor>, InMemoryRepository<Editor>>();
            builder.Services.AddSingleton<IGenericRepository<Story>, InMemoryRepository<Story>>();
            builder.Services.AddSingleton<IGenericRepository<Label>, InMemoryRepository<Label>>();
            builder.Services.AddSingleton<IGenericRepository<Note>, InMemoryRepository<Note>>();

            builder.Services.AddSingleton<IEditorService, EditorService>();
            builder.Services.AddSingleton<ILabelService, LabelService>();
            builder.Services.AddSingleton<INoteService, NoteService>();
            builder.Services.AddSingleton<IStoryService, StoryService>();*/

            builder.Services.AddControllers()
                .AddJsonOptions(options =>
                {
                    options.JsonSerializerOptions.PropertyNamingPolicy = JsonNamingPolicy.CamelCase;
                });

            builder.WebHost.UseUrls("http://localhost:24110");

            var app = builder.Build();

            app.UseRouting();
            app.UseEndpoints(endpoints =>
            {
                endpoints.MapControllers();
            });

            using (var scope = app.Services.CreateScope())
            {
                var services = scope.ServiceProvider;
                var context = services.GetRequiredService<AppDbContext>();
                context.Database.Migrate(); 
            }

            app.Run();
        }
    }
}