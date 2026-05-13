using AutoMapper;
using Publisher.Application.ViewModel;
using Publisher.Domain.Models;

namespace Publisher.Application.Profiles;

public class UserApplicationProfile : Profile
{
    public UserApplicationProfile()
    {
        CreateMap<User, UserResponseViewModel>().ReverseMap();
    }
    
}
