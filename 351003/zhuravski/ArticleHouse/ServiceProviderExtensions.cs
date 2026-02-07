using ArticleHouse.DAO.Implementation.Creator;
using ArticleHouse.DAO.Interface.Creator;
using ArticleHouse.Service.Implementation.Creator;
using ArticleHouse.Service.Interface.Creator;

namespace ArticleHouse;

static internal class ServiceProviderExtensions
{
    public static IServiceCollection AddArticleHouseServices(this IServiceCollection collection)
    {
        collection.AddScoped<ICreatorService, CreatorService>();
        collection.AddSingleton<ICreatorDAO, MemoryCreatorDAO>();
        return collection;
    }
}