using AutoMapper;
using Publisher.Domain.Models;
using Publisher.Presentation.Contracts;

namespace Publisher.Presentation.Profiles;

public class LabelProfile : Profile
{
    public LabelProfile()
    {
        CreateMap<LabelCreateRequest, Label>().ReverseMap();
        CreateMap<LabelUpdateRequest, Label>().ReverseMap();
    }
}
