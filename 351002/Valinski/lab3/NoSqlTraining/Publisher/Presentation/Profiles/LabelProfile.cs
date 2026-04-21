using AutoMapper;
using Domain.Models;
using Presentation.Contracts;

namespace Presentation.Profiles;

public class LabelProfile : Profile
{
    public LabelProfile()
    {
        CreateMap<LabelCreateRequest, Label>().ReverseMap();
        CreateMap<LabelUpdateRequest, Label>().ReverseMap();
    }
}
