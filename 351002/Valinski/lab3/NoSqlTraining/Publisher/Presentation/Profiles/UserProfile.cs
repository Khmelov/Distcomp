using AutoMapper;
using Domain.Models;
using Presentation.Contracts;

namespace Presentation.Profiles;

public class UserProfile : Profile
{
    public UserProfile()
    {
        CreateMap<UserCreateRequest, User>().ReverseMap();
        CreateMap<UserUpdateRequest, User>().ReverseMap();
    }
}
