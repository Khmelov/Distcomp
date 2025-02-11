using Application.DTO.Request;
using Application.DTO.Request.Editor;
using Application.DTO.Request.Mark;
using Application.DTO.Request.News;
using Application.DTO.Request.Post;
using Application.DTO.Response;
using Application.DTO.Response.Editor;
using Application.DTO.Response.Mark;
using Application.DTO.Response.News;
using Application.DTO.Response.Post;
using AutoMapper;
using Domain.Entities;

namespace Application.DTO.Mapper;

public class GeneralProfile : Profile
{
    public GeneralProfile()
    {
        CreateMap<EditorRequestToCreate, Editor>();
        CreateMap<EditorRequestToFullUpdate, Editor>();
        CreateMap<Editor, EditorResponseToGetById>();
        
        CreateMap<NewsRequestToCreate, News>();
        CreateMap<NewsRequestToFullUpdate, News>();
        CreateMap<News, NewsResponseToGetById>();
        
        CreateMap<PostRequestToCreate, Post>();
        CreateMap<PostRequestToFullUpdate, Post>();
        CreateMap<Post, PostResponseToGetById>();
        
        CreateMap<MarkRequestToCreate, Mark>();
        CreateMap<MarkRequestToFullUpdate, Mark>();
        CreateMap<Mark, MarkResponseToGetById>();
    }
}