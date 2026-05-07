using BlogService.Application.DTOs.Request;
using BlogService.Application.DTOs.Response;
using BlogService.Domain.Entities;

namespace BlogService.Application.Interfaces.Services;

public interface IStickerService : IService<long, StickerRequestToDto, StickerResponseToDto>
{
    
}