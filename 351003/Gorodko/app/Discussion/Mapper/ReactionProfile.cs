using AutoMapper;
using Discussion.Model;
using Discussion.DTO;

namespace Discussion.Mapping {
    public class ReactionProfile : Profile {
        public ReactionProfile() {
            CreateMap<ReactionRequestTo, Reaction>()
                .ForMember(dest => dest.Created, opt => opt.Ignore());

            CreateMap<Reaction, ReactionResponseTo>();
        }
    }
}