using AutoMapper;
using Publisher.Application.ViewModel;
using Publisher.Domain.Models;

namespace Publisher.Application.Profiles;

public class AccountApplicationProfile : Profile
{
    public AccountApplicationProfile()
    {
        CreateMap<User, AccountResponseViewModel>().ReverseMap();
    }
}
