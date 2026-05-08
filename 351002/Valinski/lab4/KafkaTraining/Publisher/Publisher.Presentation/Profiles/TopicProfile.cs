using AutoMapper;
using Publisher.Domain.Models;
using Publisher.Presentation.Contracts;

namespace Publisher.Presentation.Profiles;

public class TopicProfile : Profile
{
    public TopicProfile()
    {
        CreateMap<TopicCreateRequest, Topic>().ReverseMap();
        CreateMap<TopicUpdateRequest, Topic>().ReverseMap();
    }
}
