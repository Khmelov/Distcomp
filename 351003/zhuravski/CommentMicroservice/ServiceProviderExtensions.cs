using CommentMicroservice.Service.Interfaces;
using CommentMicroservice.Service.Implementations;
using CommentMicroservice.DAO.Implementations;
using CommentMicroservice.DAO.Interfaces;
using Additions.Service.EventService.Interfaces;
using Additions.Service.EventService.Implementations;
using CommentMicroservice.Service.Implementations.EventHandlers;

namespace CommentMicroservice;

static internal class ServiceProviderExtensions
{
    public static IServiceCollection AddCustomServices(this IServiceCollection collection)
    {
        collection.AddSingleton<ICommentService, CommentService>();
        collection.AddSingleton<ICommentDAO, CassandraCommentDAO>();
        collection.AddHttpClient<IArticleDAO, RestArticleDAO>(client =>
        {
            client.BaseAddress = new Uri("http://localhost:24110/");
        });
        collection.AddSingleton<CassandraContext>();

        collection.AddSingleton<IEventHandler, GetManyCommentsHandler>();
        collection.AddSingleton<IEventHandler, AddCommentHandler>();
        collection.AddSingleton<IEventHandler, DeleteCommentHandler>();
        collection.AddSingleton<IEventHandler, GetCommentHandler>();

        collection.AddSingleton<IEventOrchestratorService, EventOrchestratorService>();
        collection.AddSingleton<IEventProducerService, KafkaProducerService>();
        collection.AddHostedService<KafkaConsumerService>();
        return collection;
    }
}