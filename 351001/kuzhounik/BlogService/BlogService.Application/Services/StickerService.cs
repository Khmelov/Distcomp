using BlogService.Application.DTOs.Request;
using BlogService.Application.DTOs.Response;
using BlogService.Application.Interfaces.Mappers;
using BlogService.Application.Interfaces.Services;
using BlogService.Domain.Entities;
using BlogService.Domain.Interfaces;

namespace BlogService.Application.Services;

public class StickerService : BaseService<long, Sticker, StickerRequestToDto, StickerResponseToDto>, IStickerService
{
    public StickerService(IRepository<long, Sticker> repository,
        IRequestMapper<StickerRequestToDto, Sticker> userRequestMapper,
        IResponseMapper<Sticker, StickerResponseToDto> userResponseMapper) : base(repository, userRequestMapper, userResponseMapper) { }
}