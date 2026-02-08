using ArticleHouse.DAO.Implementations.Memory;
using ArticleHouse.DAO.Interfaces;
using ArticleHouse.Service.Implementations;
using ArticleHouse.Service.Interfaces;

namespace ArticleHouse;

static internal class ServiceProviderExtensions
{
    public static IServiceCollection AddArticleHouseServices(this IServiceCollection collection)
    {
        collection.AddScoped<ICreatorService, CreatorService>();
        collection.AddSingleton<ICreatorDAO, MemoryCreatorDAO>();

        collection.AddScoped<IArticleService, ArticleService>();
        collection.AddSingleton<IArticleDAO, MemoryArticleDAO>();

        collection.AddScoped<ICommentService, CommentService>();
        collection.AddSingleton<ICommentDAO, MemoryCommentDAO>();

        collection.AddScoped<IMarkService, MarkService>();
        collection.AddSingleton<IMarkDAO, MemoryMarkDAO>();
        return collection;
    }
}