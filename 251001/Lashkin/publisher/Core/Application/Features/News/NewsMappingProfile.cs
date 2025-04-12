using Application.DTO.Request;
using Application.DTO.Response;
using AutoMapper;

namespace Application.Features.News;

public class NewsMappingProfile : Profile
{
    public NewsMappingProfile()
    {
        CreateMap<NewsRequestTo, Domain.Entities.News>();

        CreateMap<Domain.Entities.News, NewsResponseTo>();
    }
}