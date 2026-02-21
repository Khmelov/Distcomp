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
            CreateMap<IssueRequestTo, Issue>();

            CreateMap<Marker, MarkerResponseTo>();
            CreateMap<MarkerRequestTo, Marker>();

            CreateMap<Note, NoteResponseTo>();
            CreateMap<NoteRequestTo, Note>();
        }
    }
}