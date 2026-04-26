using AutoMapper;
using Publisher.Application.DTOs.Requests;
using Publisher.Application.DTOs.Responses;
using Publisher.Domain.Entities;

namespace Publisher.Application.Services.Profiles;

public class MappingProfile : Profile
{
    public MappingProfile()
    {
        CreateMap<AuthorRequestTo, Author>();
        CreateMap<Author, AuthorResponseTo>();

        CreateMap<IssueRequestTo, Issue>()
            .ForMember(dest => dest.Created, opt => opt.Ignore())
            .ForMember(dest => dest.Modified, opt => opt.Ignore())
            .ForMember(dest => dest.Labels, opt => opt.Ignore());

        CreateMap<Issue, IssueResponseTo>()
            .ForMember(dest => dest.Comments, opt => opt.Ignore());

        CreateMap<LabelRequestTo, Label>();
        CreateMap<Label, LabelResponseTo>();
    }
}