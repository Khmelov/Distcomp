using ArticleHouse.DAO.Implementations;
using ArticleHouse.DAO.Interfaces;
using ArticleHouse.Service.Implementations;
using ArticleHouse.Service.Interfaces;
using Microsoft.EntityFrameworkCore;

namespace ArticleHouse;

static internal class ServiceProviderExtensions
{
    public static IServiceCollection AddArticleHouseServices(this IServiceCollection collection, string? connection)
    {
        collection.AddScoped<ICreatorService, CreatorService>();
        collection.AddScoped<ICreatorDAO, DbCreatorDAO>();

        collection.AddScoped<IArticleService, ArticleService>();
        collection.AddScoped<IArticleDAO, DbArticleDAO>();

        collection.AddScoped<ICommentService, CommentService>();
        collection.AddScoped<ICommentDAO, DbCommentDAO>();

        collection.AddScoped<IMarkService, MarkService>();
        collection.AddScoped<IMarkDAO, DbMarkDAO>();

        collection.AddScoped<IArticleMarkDAO, DbArticleMarkDAO>();

        collection.AddDbContext<ApplicationContext>(options => options.UseNpgsql(connection).UseSnakeCaseNamingConvention());
        return collection;
    }
}