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
            CreateMap<WriterRequestTo, Writer>()
                .ForMember(dest => dest.Role, opt => opt.MapFrom(src => src.Role ?? "CUSTOMER"));
            
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
            CreateMap<MessageRequestTo, Message>()
                .ForMember(dest => dest.CreatedAt, opt => opt.MapFrom(_ => DateTime.UtcNow));
            CreateMap<Message, MessageResponseTo>();

            // Reverse Mappings for Update operations
            CreateMap<WriterResponseTo, WriterRequestTo>()
                .ForMember(dest => dest.Role, opt => opt.MapFrom(src => src.Role));
            CreateMap<ArticleResponseTo, ArticleRequestTo>();
            CreateMap<MarkerResponseTo, MarkerRequestTo>();
            CreateMap<MessageResponseTo, MessageRequestTo>();
        }
    }
}
