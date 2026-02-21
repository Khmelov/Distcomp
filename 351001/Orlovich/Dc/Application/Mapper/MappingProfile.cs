using AutoMapper;
using Domain.Models;

namespace Application.Mapper;

public class MappingProfile : Profile
{
    public MappingProfile()
    {
        CreateMap<EditorRequestTo, Editor>();
        CreateMap<Editor, EditorResponseTo>();
 
        CreateMap<TagRequestTo, Tag>();
        CreateMap<Tag, TagResponseTo>();
        
        CreateMap<NoteRequestTo, Note>()
            .ForMember(dest => dest.authorId,
                opt => opt.MapFrom(src => new Editor () { id = src.authorId }));
        CreateMap<Note, NoteResponseTo>()
            .ForMember(dest => dest.authorId,
                opt => opt.MapFrom(src => src.authorId.id));
        
        CreateMap<StoryRequestTo, Story>()
            .ForMember(dest => dest.issueId,
                opt => opt.MapFrom(src => new Story(){id = src.issueId}));
        CreateMap<Story, StoryResponseTo>()
            .ForMember(dest => dest.issueId,
                opt => opt.MapFrom(src => src.issueId.id));
    }
}