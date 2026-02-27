using AutoMapper;
using Distcomp.Application.DTOs;
using Distcomp.Domain.Models;

namespace Distcomp.Application.Mapping
{
    public class MappingProfile : Profile
    {
        public MappingProfile()
        {
            CreateMap<User, UserResponseTo>();
            CreateMap<UserRequestTo, User>();

            CreateMap<Issue, IssueResponseTo>();
            CreateMap<IssueRequestTo, Issue>()
                .ForMember(dest => dest.Markers, opt => opt.Ignore());

            CreateMap<Marker, MarkerResponseTo>();
            CreateMap<MarkerRequestTo, Marker>();

            CreateMap<Note, NoteResponseTo>();
            CreateMap<NoteRequestTo, Note>();
        }
    }
}