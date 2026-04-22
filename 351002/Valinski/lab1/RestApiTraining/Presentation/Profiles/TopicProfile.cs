using Application.Dtos;
using AutoMapper;
using Presentation.Contracts.Requests;
using Presentation.Contracts.Responses;

namespace Presentation.Profiles;

public class TopicProfile : Profile
{
    public TopicProfile()
    {
        CreateMap<TopicResponseTo, TopicGetDto>().ReverseMap();
        CreateMap<TopicCreateDto, TopicRequestTo>().ReverseMap();
        CreateMap<TopicUpdateRequestTo, TopicUpdateDto>().ReverseMap();
    }
}
