using AutoMapper;
using Project.Dto;
using Project.Model;

namespace Project.Mapper {
    public class ApplicationProfile : Profile {
        public ApplicationProfile() {
            CreateMap<EditorRequestTo, Editor>()
               // .ForMember(dest => dest.Id, opt => opt.Ignore())
                .ForMember(dest => dest.Tweets, opt => opt.Ignore());

            CreateMap<Editor, EditorResponseTo>();

            CreateMap<TweetRequestTo, Tweet>()
                .ForMember(dest => dest.Editor, opt => opt.Ignore())
                .ForMember(dest => dest.Created, opt => opt.MapFrom(_ => DateTime.UtcNow))
                .ForMember(dest => dest.Modified, opt => opt.MapFrom(_ => DateTime.UtcNow))
                .ForMember(dest => dest.Reactions, opt => opt.Ignore())
                .ForMember(dest => dest.Stickers, opt => opt.Ignore());

            CreateMap<Tweet, TweetResponseTo>();

            CreateMap<StickerRequestTo, Sticker>()
                .ForMember(dest => dest.Tweets, opt => opt.Ignore());

            CreateMap<Sticker, StickerResponseTo>();

            CreateMap<ReactionRequestTo, Reaction>()
                .ForMember(dest => dest.Tweet, opt => opt.Ignore());

            CreateMap<Reaction, ReactionResponseTo>();
        }
    }
}
