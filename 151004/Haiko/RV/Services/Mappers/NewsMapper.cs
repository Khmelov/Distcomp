using AutoMapper;
using RV.Models;
using RV.Views;
using RV.Views.DTO;

namespace RV.Services.Mappers
{
    public class NewsMapper : Profile
    {
        public NewsMapper()
        {
            CreateMap<Tweet, TweetDTO>();
            CreateMap<TweetAddDTO, Tweet>().ReverseMap();
            CreateMap<Tweet, TweetUpdateDTO>().ReverseMap();
        }
    }
}
