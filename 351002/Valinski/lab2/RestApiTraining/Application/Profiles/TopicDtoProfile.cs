using Application.Dtos;
using AutoMapper;
using Domain.Models;

namespace Application.Profiles;

public class TopicDtoProfile : Profile
{
    public TopicDtoProfile()
    {
        CreateMap<Topic, TopicGetDto>().ReverseMap();
    }
}
