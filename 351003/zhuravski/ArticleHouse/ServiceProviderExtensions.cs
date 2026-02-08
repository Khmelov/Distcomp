using ArticleHouse.DAO.Implementations.Memory;
using ArticleHouse.DAO.Interfaces;
using ArticleHouse.Service.Implementation.Article;
using ArticleHouse.Service.Implementation.Creator;
using ArticleHouse.Service.Interface.Article;
using ArticleHouse.Service.Interface.Creator;

namespace ArticleHouse;

static internal class ServiceProviderExtensions
{
    public static IServiceCollection AddArticleHouseServices(this IServiceCollection collection)
    {
        collection.AddScoped<ICreatorService, CreatorService>();
        collection.AddSingleton<ICreatorDAO, MemoryCreatorDAO>();

        collection.AddScoped<IArticleService, ArticleService>();
        collection.AddSingleton<IArticleDAO, MemoryArticleDAO>();
        return collection;
    }
}