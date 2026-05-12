using BlogService.Application.DTOs.Request;
using BlogService.Application.DTOs.Response;
using BlogService.Domain.Entities;
using Shared.Application.Interfaces.Mappers;

namespace BlogService.Application.Mappers;

public class StickerMapper<Id> : IRequestMapper<StickerRequestToDto<Id>, Sticker<Id>>, IResponseMapper<Sticker<Id>, StickerResponseToDto<Id>>
{
    public Sticker<Id> Map(StickerRequestToDto<Id> dto)
    {
        return new Sticker<Id>()
        {
            ID =  dto.ID,
            Text =  dto.Name,
        };
    }

    public StickerResponseToDto<Id> Map(Sticker<Id> entity)
    {
        return new StickerResponseToDto<Id>()
        {
            ID = entity.ID,
            Name = entity.Text,
        };
    }
}