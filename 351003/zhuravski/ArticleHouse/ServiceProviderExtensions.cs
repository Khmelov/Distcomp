using ArticleHouse.DAO;
using ArticleHouse.Service.Implementations;
using ArticleHouse.Service.Interfaces;
using Microsoft.EntityFrameworkCore;

namespace ArticleHouse;

static internal class ServiceProviderExtensions
{
    public static IServiceCollection AddArticleHouseServices(this IServiceCollection collection, string? connection)
    {
        collection.AddScoped<ICreatorService, CreatorService>();
        collection.AddScoped<IArticleService, ArticleService>();
        collection.AddScoped<ICommentService, CommentService>();
        collection.AddScoped<IMarkService, MarkService>();

        collection.AddDbContext<ApplicationContext>(options => options.UseNpgsql(connection).UseSnakeCaseNamingConvention());
        return collection;
    }
}