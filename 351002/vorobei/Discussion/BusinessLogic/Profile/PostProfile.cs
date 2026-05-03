using BusinessLogic.DTO.Request;
using BusinessLogic.DTO.Response;
using DataAccess.Models;
using AutoMapper;

namespace BusinessLogic.Profiles
{
    public class PostProfile : Profile
    {
        public PostProfile()
        {
            CreateMap<PostRequestTo, PostByStory>();
            CreateMap<PostByStory, PostResponseTo>();

            CreateMap<PostRequestTo, PostById>();
            CreateMap<PostById, PostResponseTo>();

            CreateMap<PostRequestTo, PostResponseTo>();
        }
    }
}
