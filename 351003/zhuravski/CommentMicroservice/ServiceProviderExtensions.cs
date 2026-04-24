using CommentMicroservice.Service.Interfaces;
using CommentMicroservice.Service.Implementations;
using CommentMicroservice.DAO.Implementations;
using CommentMicroservice.DAO.Interfaces;
using CommentMicroservice.Service.Implementations.EventHandlers;
using Additions.Messaging.Implementations;
using Additions.Messaging.Interfaces;

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
        collection.AddSingleton<IEventHandler, UpdateCommentHandler>();
        collection.AddSingleton<IEventHandler, ArticleDeletedHandler>();

        collection.AddSingleton<IEventOrchestrator, EventOrchestrator>();
        collection.AddSingleton<IEventProducer, KafkaProducer>();
        collection.AddHostedService<KafkaConsumer>();
        return collection;
    }
}