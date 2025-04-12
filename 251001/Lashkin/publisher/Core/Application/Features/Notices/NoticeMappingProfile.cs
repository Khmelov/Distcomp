using Application.DTO.Request;
using Application.DTO.Response;
using AutoMapper;
using Domain.Entities;

namespace Application.Features.Notices;

public class NoticeMappingProfile : Profile
{
    public NoticeMappingProfile()
    {
        CreateMap<NoticeRequestTo, Notice>();

        CreateMap<Notice, NoticeResponseTo>();
    }
}