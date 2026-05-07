using BlogService.Application.DTOs.Request;
using BlogService.Application.DTOs.Response;
using Microsoft.Extensions.DependencyInjection;
using BlogService.Application.Interfaces.Mappers;
using BlogService.Application.Interfaces.Services;
using BlogService.Application.Mappers;
using BlogService.Application.Services;
using BlogService.Domain.Entities;
using BlogService.Domain.Interfaces;
using BlogService.Infrastructure.Storage.Repositories;

namespace BlogService.Application.DependencyInjection;

public static class ApplicationServiceCollectionExtensions
{
    public static IServiceCollection AddApplication(this IServiceCollection services)
    {
        // Репозиторий
        services.AddSingleton(typeof(IRepository<,>), typeof(InMemoryRepository<,>));

        // Сервисы
        services.AddScoped<IUserService, UserService>();
        services.AddScoped<IStoryService, StoryService>();
        services.AddScoped<ICommentService, CommentService>();
        services.AddScoped<IStickerService, StickerService>();
        
        // Мапперы
        services.AddScoped<IRequestMapper<UserRequestToDto, User>, UserMapper>();
        services.AddScoped<IResponseMapper<User, UserResponseToDto>, UserMapper>();
        
        services.AddScoped<IRequestMapper<StoryRequestToDto, Story>, StoryMapper>();
        services.AddScoped<IResponseMapper<Story, StoryResponseToDto>, StoryMapper>();
        
        services.AddScoped<IRequestMapper<CommentRequestToDto, Comment>, CommentMapper>();
        services.AddScoped<IResponseMapper<Comment, CommentResponseToDto>, CommentMapper>();
        
        services.AddScoped<IRequestMapper<StickerRequestToDto, Sticker>, StickerMapper>();
        services.AddScoped<IResponseMapper<Sticker, StickerResponseToDto>, StickerMapper>();
        
        return services;
    }
}