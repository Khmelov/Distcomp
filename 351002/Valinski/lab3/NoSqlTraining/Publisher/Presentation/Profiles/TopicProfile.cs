using AutoMapper;
using Domain.Models;
using Presentation.Contracts;

namespace Presentation.Profiles;

public class TopicProfile : Profile
{
    public TopicProfile()
    {
        CreateMap<TopicCreateRequest, Topic>().ReverseMap();
        CreateMap<TopicUpdateRequest, Topic>().ReverseMap();
    }
}
