using AutoMapper;
using Publisher.Application.ViewModel;
using Publisher.Presentation.Contracts;

namespace Publisher.Presentation.Profiles;

public class TopicProfile : Profile
{
    public TopicProfile()
    {
        CreateMap<TopicResponse, TopicResponseViewModel>().ReverseMap();
    }
}
