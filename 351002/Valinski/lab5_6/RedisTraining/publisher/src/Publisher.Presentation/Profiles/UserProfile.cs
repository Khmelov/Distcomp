using AutoMapper;
using Publisher.Application.ViewModel;
using Publisher.Presentation.Contracts;

namespace Publisher.Presentation.Profiles;

public class UserProfile : Profile
{
    public UserProfile()
    {
        CreateMap<UserResponse, UserResponseViewModel>().ReverseMap();
    }
}
