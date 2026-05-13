using AutoMapper;
using lab1.Models.DTO.Requests;
using lab1.Models.DTO.Responses;
using lab1.Models.Entities;

namespace lab1.Mapping;

public class EditorProfile : Profile
{
    public EditorProfile()
    {
        CreateMap<EditorRequestTo, Editor>()
            .ForMember(d => d.FirstName, o => o.MapFrom(s => s.Firstname))
            .ForMember(d => d.LastName, o => o.MapFrom(s => s.Lastname))
            .ForMember(d => d.Role, o => o.MapFrom(s => string.IsNullOrWhiteSpace(s.Role) ? "CUSTOMER" : s.Role!.ToUpperInvariant()));

        CreateMap<Editor, EditorResponseTo>()
            .ForMember(d => d.Firstname, o => o.MapFrom(s => s.FirstName))
            .ForMember(d => d.Lastname, o => o.MapFrom(s => s.LastName));
    }
}
