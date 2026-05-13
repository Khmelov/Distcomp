using AutoMapper;
using Publisher.Application.ViewModel;
using Publisher.Presentation.Contracts;

namespace Publisher.Presentation.Profiles;

public class LabelProfile : Profile
{
    public LabelProfile()
    {
        CreateMap<LabelResponse, LabelResponseViewModel>().ReverseMap();
    }
}
