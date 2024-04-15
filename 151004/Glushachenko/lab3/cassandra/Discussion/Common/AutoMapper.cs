using AutoMapper;
using Discussion.PostEntity;

namespace Discussion.Common
{
    public class AutoMapper : Profile
    {
        public AutoMapper()
        {
            CreateMap<PostRequestTO, Post>();
            CreateMap<Post, PostResponseTO>();
        }
    }
}
