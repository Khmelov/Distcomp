using AutoMapper;
using Domain.Models;

namespace Application.Mapper;

public class MappingProfile : Profile
{
    public MappingProfile()
    {
        CreateMap<AuthorRequestTo, Author>();
        CreateMap<Author, AuthorResponseTo>();

        CreateMap<MarkerRequestTo, Marker>();
        CreateMap<Marker, MarkerResponseTo>();

        CreateMap<IssueRequestTo, Issue>();
        CreateMap<Issue, IssueResponseTo>();

        CreateMap<CommentRequestTo, Comment>();
        CreateMap<Comment, CommentResponseTo>();

        //CreateMap<IssueRequestTo, Issue>()
        //    .ForMember(dest => dest.authorId,
        //        opt => opt.MapFrom(src => new Author() { id = src.authorId }));
        //CreateMap<Issue, IssueResponseTo>()
        //    .ForMember(dest => dest.authorId,
        //        opt => opt.MapFrom(src => src.author.id));

        //CreateMap<CommentRequestTo, Comment>()
        //    .ForMember(dest => dest.issueId,
        //        opt => opt.MapFrom(src => new Issue() { id = src.issueId }));
        //CreateMap<Comment, CommentResponseTo>()
        //    .ForMember(dest => dest.issueId,
        //        opt => opt.MapFrom(src => src.issue.id));
    }
}