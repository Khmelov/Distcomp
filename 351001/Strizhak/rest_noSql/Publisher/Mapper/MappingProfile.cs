using AutoMapper;
using Publisher.Dtos;
using Publisher.Entities;
using Publisher.Dtos;

namespace Publisher.Mapper
{
    public class MappingProfile : Profile
    {
        public MappingProfile()
        {
            // User
            CreateMap<User, UserResponseTo>();
            CreateMap<UserRequestTo, User>()
                .ForMember(dest => dest.Id, opt => opt.Ignore()) 
                .ForMember(dest => dest.Password, opt => opt.Ignore()); 

            // Topic
            CreateMap<Topic, TopicResponseTo>();
            CreateMap<TopicRequestTo, Topic>()
                .ForMember(dest => dest.Id, opt => opt.Ignore())
                .ForMember(dest => dest.Created, opt => opt.Ignore())
                .ForMember(dest => dest.Modified, opt => opt.Ignore());

            // Tag
            CreateMap<Tag, TagResponseTo>();
            CreateMap<TagRequestTo, Tag>()
                .ForMember(dest => dest.Id, opt => opt.Ignore());
        }
    }
}