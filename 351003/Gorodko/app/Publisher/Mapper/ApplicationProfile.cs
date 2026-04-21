using AutoMapper;
using Publisher.Dto;
using Publisher.Model;

namespace Publisher.Mapper {
    public class ApplicationProfile : Profile {
        public ApplicationProfile() {
            CreateMap<EditorRequestTo, Editor>()
               // .ForMember(dest => dest.Id, opt => opt.Ignore())
                .ForMember(dest => dest.Tweets, opt => opt.Ignore());

            CreateMap<Editor, EditorResponseTo>();

            CreateMap<Tweet, TweetResponseTo>()
                .ForMember(dest => dest.Stickers, opt => opt.Ignore());

            CreateMap<TweetRequestTo, Tweet>()
                .ForMember(dest => dest.Id, opt => opt.Ignore())
                .ForMember(dest => dest.Created, opt => opt.Ignore())
                .ForMember(dest => dest.Modified, opt => opt.Ignore())
                .ForMember(dest => dest.Editor, opt => opt.Ignore())
                .ForMember(dest => dest.Reactions, opt => opt.Ignore())
                .ForMember(dest => dest.TweetStickers, opt => opt.Ignore());

            CreateMap<StickerRequestTo, Sticker>()
                .ForMember(dest => dest.Tweets, opt => opt.Ignore());

            CreateMap<Sticker, StickerResponseTo>();

            CreateMap<ReactionRequestTo, Reaction>()
                .ForMember(dest => dest.Tweet, opt => opt.Ignore());

            CreateMap<Reaction, ReactionResponseTo>();
        }
    }
}
