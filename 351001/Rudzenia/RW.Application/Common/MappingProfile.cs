using AutoMapper;
using RW.Application.DTOs.Request;
using RW.Application.DTOs.Response;
using RW.Domain.Entities;

namespace RW.Application.Common;

public class MappingProfile : Profile
{
    public MappingProfile()
    {
        // Author
        CreateMap<AuthorRequestTo, Author>();
        CreateMap<Author, AuthorResponseTo>();

        // Article
        CreateMap<ArticleRequestTo, Article>();
        CreateMap<Article, ArticleResponseTo>();

        // Tag
        CreateMap<TagRequestTo, Tag>();
        CreateMap<Tag, TagResponseTo>();

        // Note mappings removed - Notes are handled by Discussion microservice
    }
}
