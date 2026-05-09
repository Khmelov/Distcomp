using Application.Dtos;
using AutoMapper;
using Presentation.Contracts.Requests;
using Presentation.Contracts.Responses;

namespace Presentation.Profiles;

public class LabelProfile : Profile
{
    public LabelProfile()
    {
        CreateMap<LabelResponseTo, LabelGetDto>().ReverseMap();
        CreateMap<LabelRequestTo, LabelCreateDto>().ReverseMap();
        CreateMap<LabelUpdateRequestTo, LabelUpdateDto>().ReverseMap();
    }
}
