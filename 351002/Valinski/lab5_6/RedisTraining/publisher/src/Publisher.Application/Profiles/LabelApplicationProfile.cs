using AutoMapper;
using Publisher.Application.ViewModel;
using Publisher.Domain.Models;

namespace Publisher.Application.Profiles;

public class LabelApplicationProfile : Profile
{
    public LabelApplicationProfile()
    {
        CreateMap<Label, LabelResponseViewModel>().ReverseMap();
    }
}
