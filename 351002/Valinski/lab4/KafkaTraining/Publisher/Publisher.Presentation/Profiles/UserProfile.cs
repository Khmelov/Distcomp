using AutoMapper;
using Publisher.Domain.Models;
using Publisher.Presentation.Contracts;

namespace Publisher.Presentation.Profiles;

public class UserProfile : Profile
{
    public UserProfile()
    {
        CreateMap<UserCreateRequest, User>().ReverseMap();
        CreateMap<UserUpdateRequest, User>().ReverseMap();
    }
}
