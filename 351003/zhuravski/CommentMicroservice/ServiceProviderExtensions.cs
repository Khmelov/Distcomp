using CommentMicroservice.Service.Interfaces;
using CommentMicroservice.Service.Implementations;
using CommentMicroservice.DAO.Implementations;
using CommentMicroservice.DAO.Interfaces;

namespace CommentMicroservice;

static internal class ServiceProviderExtensions
{
    public static IServiceCollection AddCustomServices(this IServiceCollection collection)
    {
        collection.AddScoped<ICommentService, CommentService>();
        collection.AddScoped<ICommentDAO, CassandraCommentDAO>();
        collection.AddScoped<CassandraContext>();
        return collection;
    }
}