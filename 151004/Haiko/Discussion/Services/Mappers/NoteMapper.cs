using AutoMapper;
using Discussion.Models;
using Discussion.Views.DTO;

namespace Discussion.Services.Mappers
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
