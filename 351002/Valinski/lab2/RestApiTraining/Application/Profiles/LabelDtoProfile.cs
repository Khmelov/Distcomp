using Application.Dtos;
using AutoMapper;
using Domain.Models;

namespace Application.Profiles;

public class LabelDtoProfile : Profile
{
    public LabelDtoProfile()
    {
        CreateMap<Label, LabelGetDto>().ReverseMap();
    }
}
