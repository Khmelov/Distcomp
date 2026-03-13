using AutoMapper;
using DistComp.Application.DTOs.Requests;
using DistComp.Application.DTOs.Responses;
using DistComp.Domain.Entities;

namespace DistComp.Application.Services.Profiles;

public class MappingProfile : Profile {
    public MappingProfile() {
        CreateMap<AuthorRequestTo, Author>();
        CreateMap<Author, AuthorResponseTo>();

        CreateMap<IssueRequestTo, Issue>()
            .ForMember(dest => dest.Created, opt => opt.Ignore())
            .ForMember(dest => dest.Modified, opt => opt.Ignore());

        CreateMap<Issue, IssueResponseTo>();

        CreateMap<LabelRequestTo, Label>();
        CreateMap<Label, LabelResponseTo>();

        CreateMap<CommentRequestTo, Comment>();
        CreateMap<Comment, CommentResponseTo>();
    }
}