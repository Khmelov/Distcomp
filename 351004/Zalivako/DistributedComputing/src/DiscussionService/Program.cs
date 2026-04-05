using DiscussionService.Interfaces;
using DiscussionService.MappingProfiles;
using DiscussionService.Repositories;
using DiscussionService.Services;
using MongoDB.Driver;

namespace DiscussionService
{
    public class Program
    {
        public static void Main(string[] args)
        {
            var builder = WebApplication.CreateBuilder(args);

            // Add services to the container.

            // MongoDB
            var mongoClient = new MongoClient("mongodb://localhost:27017");
            var mongoDatabase = mongoClient.GetDatabase("distcomp");

            builder.Services.AddSingleton(mongoDatabase);
            builder.Services.AddScoped<IPostRepository, PostRepository>();
            builder.Services.AddScoped<IPostService, PostService>();
            builder.Services.AddAutoMapper(
            config => {
                config.AddProfile<PostProfile>();
            });

            builder.Services.AddControllers();

            var app = builder.Build();

            // Configure the HTTP request pipeline.

            app.UseAuthorization();

            app.MapControllers();

            app.Run();
        }
    }
}
