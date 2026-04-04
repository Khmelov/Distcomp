using CommentMicroservice.Service.Interfaces;
using CommentMicroservice.Service.Implementations;
using CommentMicroservice.DAO.Implementations;
using CommentMicroservice.DAO.Interfaces;
using Additions.Service.EventService.Interfaces;
using Additions.Service.EventService.Implementations;

namespace CommentMicroservice;

static internal class ServiceProviderExtensions
{
    public static IServiceCollection AddCustomServices(this IServiceCollection collection)
    {
        collection.AddScoped<ICommentService, CommentService>();
        collection.AddScoped<ICommentDAO, CassandraCommentDAO>();
        collection.AddHttpClient<IArticleDAO, RestArticleDAO>(client =>
        {
            client.BaseAddress = new Uri("http://localhost:24110/");
        });
        collection.AddScoped<CassandraContext>();

        collection.AddSingleton<IEventOrchestratorService, EventOrchestratorService>();
        collection.AddSingleton<IEventProducerService, KafkaProducerService>();
        return collection;
    }
}