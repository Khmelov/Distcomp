using AutoMapper;
using Discussion.Entities;
using Shared.Dtos;


namespace Discussion
{
    public class MappingProfile : Profile
    {
        public MappingProfile()
        {
            CreateMap<Reaction, ReactionResponseTo>();
            CreateMap<ReactionRequestTo, Reaction>()
                .ForMember(dest => dest.Id, opt => opt.Ignore());
        }
    }
}