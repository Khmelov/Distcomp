using AutoMapper;
using RestApiTask.Models.DTOs;
using RestApiTask.Models.Entities;

namespace RestApiTask.Mappings
{
   
    public class MappingProfile : Profile
    {
        public MappingProfile()
        {
            // Writer Mappings
            CreateMap<WriterRequestTo, Writer>();
            CreateMap<Writer, WriterResponseTo>();

            // Article Mappings
            CreateMap<ArticleRequestTo, Article>()
                .ForMember(dest => dest.Created, opt => opt.MapFrom(_ => DateTime.UtcNow))
                .ForMember(dest => dest.Modified, opt => opt.MapFrom(_ => DateTime.UtcNow));
            CreateMap<Article, ArticleResponseTo>();

            // Marker Mappings
            CreateMap<MarkerRequestTo, Marker>();
            CreateMap<Marker, MarkerResponseTo>();

            // Message Mappings
            CreateMap<MessageRequestTo, Message>();
            CreateMap<Message, MessageResponseTo>();

            CreateMap<WriterResponseTo, WriterRequestTo>();
            CreateMap<ArticleResponseTo, ArticleRequestTo>();
            CreateMap<MarkerResponseTo, MarkerRequestTo>();
            CreateMap<MessageResponseTo, MessageRequestTo>();
        }
    }
}
