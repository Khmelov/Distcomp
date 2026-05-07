using Application.DTOs.Requests;
using Application.DTOs.Responses;
using AutoMapper;
using Core.Entities;

namespace Application.MappingProfiles
{
    public class MarkerProfile : Profile
    {
        public MarkerProfile()
        {
            CreateMap<MarkerRequestTo, Marker>()
                .ForMember(dest => dest.Id, opt => opt.MapFrom(src => src.Id ?? 0))
                .ForMember(dest => dest.Name, opt => opt.MapFrom(src => src.Name));

            CreateMap<Marker, MarkerResponseTo>()
                .ForMember(dest => dest.Id, opt => opt.MapFrom(src => src.Id))
                .ForMember(dest => dest.Name, opt => opt.MapFrom(src => src.Name));
        }
    }
}
