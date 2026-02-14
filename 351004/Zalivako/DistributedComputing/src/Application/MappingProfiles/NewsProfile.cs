using Application.DTOs.Requests;
using Application.DTOs.Responses;
using AutoMapper;
using Core.Entities;

namespace Application.MappingProfiles
{
    public class NewsProfile : Profile
    {
        public NewsProfile()
        {
            CreateMap<NewsRequestTo, News>()
                .ForMember(dest => dest.Id, opt => opt.MapFrom(src => src.Id ?? 0))
                .ForMember(dest => dest.CreatedAt, opt => opt.MapFrom(src => DateTime.UtcNow))
                .ForMember(dest => dest.Modified, opt => opt.MapFrom(src => DateTime.UtcNow))
                .ForMember(dest => dest.EditorId, opt => opt.MapFrom(src => src.EditorId))
                .ForMember(dest => dest.Content, opt => opt.MapFrom(src => src.Content))
                .ForMember(dest => dest.Title, opt => opt.MapFrom(src => src.Title));

            CreateMap<News, NewsResponseTo>()
                .ForMember(dest => dest.Id, opt => opt.MapFrom(src => src.Id))
                .ForMember(dest => dest.EditorId, opt => opt.MapFrom(src => src.EditorId))
                .ForMember(dest => dest.Title, opt => opt.MapFrom(src => src.Title))
                .ForMember(dest => dest.Content, opt => opt.MapFrom(src => src.Content))
                .ForMember(dest => dest.CreatedAt, opt => opt.MapFrom(src => src.CreatedAt))
                .ForMember(dest => dest.Modified, opt => opt.MapFrom(src => src.Modified));
        }
    }
}
