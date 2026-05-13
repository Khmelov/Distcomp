using AutoMapper;
using Publisher.Application.ViewModel;
using Publisher.Domain.Models;

namespace Publisher.Application.Profiles;

public class TopicApplicationProfile : Profile
{
    public TopicApplicationProfile()
    {
        CreateMap<Topic, TopicResponseViewModel>().ReverseMap();
    }
}
