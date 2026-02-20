
using AutoMapper;
using Task310RestApi.DTOs.Request;
using Task310RestApi.DTOs.Response;
using Task310RestApi.Models;

namespace Task310RestApi.Mappers
{
    public class AutoMapperProfile : Profile
    {
        public AutoMapperProfile()
        {
            // Creator mappings
            CreateMap<CreatorRequestTo, Creator>();
            CreateMap<Creator, CreatorResponseTo>();
            
            // News mappings
            CreateMap<NewsRequestTo, News>()
                .ForMember(dest => dest.LabelIds, opt => opt.MapFrom(src => src.LabelIds ?? new List<long>()));
            CreateMap<News, NewsResponseTo>();
            
            // Label mappings
            CreateMap<LabelRequestTo, Label>();
            CreateMap<Label, LabelResponseTo>();
            
            // Post mappings
            CreateMap<PostRequestTo, Post>();
            CreateMap<Post, PostResponseTo>();
        }
    }
}
