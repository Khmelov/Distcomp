using CommentMicroservice.Service.Interfaces;
using CommentMicroservice.Service.Implementations;

namespace CommentMicroservice;

static internal class ServiceProviderExtensions
{
    public static IServiceCollection AddCustomServices(this IServiceCollection collection)
    {
        collection.AddScoped<ICommentService, CommentService>();

        return collection;
    }
}