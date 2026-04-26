using AutoMapper;
using Discussion.Domain.Entities;
using Shared.DTOs.Requests;
using Shared.DTOs.Responses;
using Shared.Enums;

namespace Discussion.Application.Services.Profiles;

public class MappingProfile : Profile
{
    public MappingProfile()
    {
        CreateMap<CommentRequestTo, Comment>()
            .ForMember(dest => dest.State, opt => opt.MapFrom(src => src.State.ToString()));


        CreateMap<Comment, CommentResponseTo>()
            .ForMember(dest => dest.State, opt => opt.MapFrom(src =>
                Enum.Parse<CommentState>(src.State)));
    }
}