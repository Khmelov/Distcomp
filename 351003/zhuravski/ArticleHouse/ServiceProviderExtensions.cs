using ArticleHouse.DAO.Implementation.Article;
using ArticleHouse.DAO.Implementation.Creator;
using ArticleHouse.DAO.Interface.Article;
using ArticleHouse.DAO.Interface.Creator;
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