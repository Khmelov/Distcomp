using AutoMapper;
using DiscussionModule.DTOs.requests;
using DiscussionModule.DTOs.responses;
using DiscussionModule.models;

namespace DiscussionModule.mappers;

public class NoteProfile : Profile
{
    public NoteProfile()
    {
        CreateMap<NoteRequestTo, Note>()
            .ForMember(dest => dest.Id, opt => opt.MapFrom(src => src.Id ?? 0))
            .ForMember(dest => dest.NewsId, opt => opt.MapFrom(src => src.NewsId));
            // .ForMember(dest => dest.State, opt => opt.MapFrom(src => src.State ?? "PENDING"));

            CreateMap<Note, NoteResponseTo>()
                .ForMember(dest => dest.Id, opt => opt.MapFrom(src => src.Id))
                .ForMember(dest => dest.NewsId, opt => opt.MapFrom(src => src.NewsId));
            // .ForMember(dest => dest.State, opt => opt.MapFrom(src => src.State));
    }
}