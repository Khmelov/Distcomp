using Application.DTOs.Requests;
using Application.DTOs.Responses;
using AutoMapper;
using Core.Entities;

namespace Application.MappingProfiles
{
    public class PostProfile : Profile
    {
        public PostProfile()
        {
            CreateMap<PostRequestTo, Post>()
                .ForMember(dest => dest.Id, opt => opt.MapFrom(src => src.Id ?? 0))
                .ForMember(dest => dest.NewsId, opt => opt.MapFrom(src => src.NewsId));

            CreateMap<Post, PostResponseTo>()
                .ForMember(dest => dest.Id, opt => opt.MapFrom(src => src.Id))
                .ForMember(dest => dest.NewsId, opt => opt.MapFrom(src => src.NewsId));
        }
    }
}
