using BlogService.Application.DTOs.Request;
using BlogService.Application.DTOs.Response;
using Shared.Application.Interfaces.Services;

namespace BlogService.Application.Interfaces.Services;

public interface IStickerService<Id> : IService<Id, StickerRequestToDto<Id>, StickerResponseToDto<Id>>
{
    
}