using BlogService.Application.DTOs.Request;
using BlogService.Application.DTOs.Response;
using BlogService.Application.Interfaces.Services;
using BlogService.Domain.Entities;
using Microsoft.Extensions.Caching.Distributed;
using Shared.Application.Interfaces.Mappers;
using Shared.Application.Services;
using Shared.Domain.Interfaces;

namespace BlogService.Application.Services;

public class StickerService<Id> : BaseService<Id, Sticker<Id>, StickerRequestToDto<Id>, StickerResponseToDto<Id>>, IStickerService<Id>
{
    public StickerService(IRepository<Id, Sticker<Id>> repository,
        IRequestMapper<StickerRequestToDto<Id>, Sticker<Id>> userRequestMapper,
        IResponseMapper<Sticker<Id>, StickerResponseToDto<Id>> userResponseMapper, 
        IDistributedCache cache) : 
        base(repository, userRequestMapper, userResponseMapper, cache) { }
}