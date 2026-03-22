using AutoMapper;
using BusinessLogic.DTO.Request;
using BusinessLogic.DTO.Response;
using DataAccess.Models;

namespace BusinessLogic.Profiles
{
    public class UserProfile : Profile
    {
        public UserProfile() 
        {
            CreateMap<CreatorRequestTo, Creator>();
            CreateMap<Creator, CreatorResponseTo>();

            CreateMap<MarkRequestTo, Mark>();
            CreateMap<Mark, MarkResponseTo>();

            CreateMap<PostRequestTo, Post>();
            CreateMap<Post, PostResponseTo>();

            CreateMap<StoryRequestTo, Story>();
            CreateMap<Story, StoryResponseTo>();
        }        
    }
}
