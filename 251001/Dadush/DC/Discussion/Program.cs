using Cassandra;
using ISession = Cassandra.ISession;
using Discussion.Data;
using Discussion.Models;
using Discussion.Data.Implementation;
using Discussion.Services;
using Discussion.Services.Implementation;
using Discussion.Models.Mapping;

namespace Discussion {
    public class Program {
        public static void Main(string[] args) {
            var builder = WebApplication.CreateBuilder(args);

            // Add services to the container.

            builder.Services.AddControllers();

            // Add Cassandra
            builder.Services.AddSingleton<Cluster>(Cluster.Builder()
                .AddContactPoints(builder.Configuration.GetConnectionString("cassandra"))
                .Build()
            );
            builder.Services.AddSingleton<ICommentRepository, CommentRepository>();

            // Add DTO mapping
            builder.Services.AddDtoMapping();

            // Add services
            builder.Services.AddTransient<ICommentService, CommentService>();

            var app = builder.Build();

            // Configure the HTTP request pipeline.
            if (app.Environment.IsDevelopment()) {
                app.MapOpenApi();
            }

            app.UseAuthorization();

            app.MapControllers();

            app.Run();
        }
    }
}
