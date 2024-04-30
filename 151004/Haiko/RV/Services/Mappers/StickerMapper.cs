using AutoMapper;
using RV.Models;
using RV.Views.DTO;
using System;

namespace RV.Services.Mappers
{
    public class StickerMapper : Profile
    {
        public StickerMapper()
        {
            CreateMap<Sticker, StickerDTO>();
            CreateMap<StickerAddDTO, Sticker>().ReverseMap();
            CreateMap<Sticker, StickerUpdateDTO>().ReverseMap();
        }
    }
}
