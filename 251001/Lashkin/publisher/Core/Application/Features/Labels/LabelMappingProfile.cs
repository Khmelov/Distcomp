using Application.DTO.Request;
using Application.DTO.Response;
using AutoMapper;
using Domain.Entities;

namespace Application.Features.Labels;

public class LabelMappingProfile : Profile
{
    public LabelMappingProfile()
    {
        CreateMap<LabelRequestTo, Label>();

        CreateMap<Label, LabelResponseTo>();
    }
}