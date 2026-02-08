using AutoMapper;
using Domain.Models;

namespace Application.Mapper;

public class MappingProfile : Profile
{
    public MappingProfile()
    {
        CreateMap<EditorRequestTo, Editor>();

        CreateMap<Editor, EditorResponseTo>();
    }
}