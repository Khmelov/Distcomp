using BlogService.Application.DTOs.Request;
using BlogService.Application.DTOs.Response;
using BlogService.Application.Interfaces.Mappers;
using BlogService.Domain.Entities;

namespace BlogService.Application.Mappers;

public class StickerMapper : IRequestMapper<StickerRequestToDto, Sticker>, IResponseMapper<Sticker, StickerResponseToDto>
{
    public Sticker Map(StickerRequestToDto dto)
    {
        return new Sticker()
        {
            ID =  dto.ID,
            Text =  dto.Name,
        };
    }

    public StickerResponseToDto Map(Sticker entity)
    {
        return new StickerResponseToDto()
        {
            ID = entity.ID,
            Name = entity.Text,
        };
    }
}