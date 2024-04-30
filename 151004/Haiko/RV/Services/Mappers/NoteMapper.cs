using AutoMapper;
using RV.Models;
using RV.Views.DTO;

namespace RV.Services.Mappers
{
    public class NoteMapper : Profile
    {
        public NoteMapper()
        {
            CreateMap<Note, NoteDTO>();
            CreateMap<NoteAddDTO, Note>().ReverseMap();
            CreateMap<Note, NoteUpdateDTO>().ReverseMap();
        }
    }
}
