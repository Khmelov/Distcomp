using Application.Dtos;
using AutoMapper;
using Presentation.Contracts.Requests;
using Presentation.Contracts.Responses;

namespace Presentation.Profiles;

public class ReactionProfile : Profile
{
    public ReactionProfile()
    {
        CreateMap<ReactionResponseTo, ReactionGetDto>().ReverseMap();
        CreateMap<ReactionRequestTo, ReactionCreateDto>().ReverseMap();
        CreateMap<ReactionUpdateRequestTo, ReactionUpdateDto>().ReverseMap();
    }
}
