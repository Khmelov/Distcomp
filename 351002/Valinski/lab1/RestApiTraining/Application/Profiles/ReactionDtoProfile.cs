using Application.Dtos;
using AutoMapper;
using Domain.Models;

namespace Application.Profiles;

public class ReactionDtoProfile : Profile
{
    public ReactionDtoProfile()
    {
        CreateMap<Reaction, ReactionGetDto>().ReverseMap();
    }
}
