using Additions.Cache.Implementations;
using Additions.Cache.Interfaces;
using Additions.Messaging.Implementations;
using Additions.Messaging.Interfaces;
using ArticleHouse.DAO.Implementations;
using ArticleHouse.DAO.Interfaces;
using ArticleHouse.Service.Implementations;
using ArticleHouse.Service.Interfaces;
using Microsoft.EntityFrameworkCore;
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