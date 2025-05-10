using Microsoft.EntityFrameworkCore;
using StackExchange.Redis;
using System.Text.Json;
using System.Text.Json.Serialization;
using WebApplication1.Data;
using WebApplication1.Entity;
using WebApplication1.Middleware;
using WebApplication1.Repository;
using WebApplication1.Service;

namespace WebApplication1
{
    public partial class Program
    {
        public static async Task Main(string[] args)
        {
            var builder = WebApplication.CreateBuilder(args);

            var connectionString = builder.Configuration.GetConnectionString("DefaultConnection");
            builder.Services.AddDbContext<AppDbContext>(options =>
    options.UseNpgsql(builder.Configuration.GetConnectionString("DefaultConnection")));

            builder.Services.AddScoped<IRepository<Editor>, EfRepository<Editor>>();
            builder.Services.AddScoped<IRepository<Topic>, EfRepository<Topic>>();
            builder.Services.AddScoped<IRepository<Tag>, EfRepository<Tag>>();
            builder.Services.AddScoped<IRepository<Reaction>, EfRepository<Reaction>>();

            var redis = ConnectionMultiplexer.Connect("localhost:6379");
            builder.Services.AddSingleton<IConnectionMultiplexer>(redis);
            builder.Services.AddSingleton<IRedisCacheService, RedisCacheService>();

            builder.Services.AddScoped<IEditorService, EditorService>();
            builder.Services.AddScoped<ITopicService, TopicService>();
            builder.Services.AddScoped<ITagService, TagService>();

            builder.Services.AddScoped<IRemoteReactionService, RemoteReactionService>();
            builder.Services.AddHttpClient<IRemoteReactionService, RemoteReactionService>(client =>
            {
                client.BaseAddress = new Uri("http://localhost:24130/api/v1.0/");
            });

            builder.Services.AddSingleton<KafkaReactionProducerService>();
            builder.Services.AddSingleton<KafkaReactionStatusUpdaterService>();

            builder.Services.AddControllers()
                .AddJsonOptions(options =>
                {
                    options.JsonSerializerOptions.PropertyNamingPolicy = JsonNamingPolicy.CamelCase;
                });


            builder.Services.AddLogging(logging =>
            {
                logging.ClearProviders();
                logging.AddConsole();
                logging.SetMinimumLevel(LogLevel.Information);
            });

            var app = builder.Build();

            app.UseMiddleware<ErrorHandlingMiddleware>();

            app.MapControllers();

            app.Urls.Add("http://localhost:24110");

            using (var scope = app.Services.CreateScope())
            {
                var editorService = scope.ServiceProvider.GetRequiredService<IEditorService>();
                try
                {
                    await editorService.CreateEditorAsync(new DTO.EditorRequestTo
                    {
                        Login = "milena@gmail.com",
                        Password = "123456",
                        Firstname = "Милена",
                        Lastname = "Короткая"
                    });
                }
                catch (ValidationException)
                {
                }
            }

            var kafkaConsumer = app.Services.GetRequiredService<KafkaReactionStatusUpdaterService>();
            var kafkaCts = new CancellationTokenSource();
            Task.Run(() => kafkaConsumer.RunAsync(kafkaCts.Token));

            await app.RunAsync();
        }
    }
}
