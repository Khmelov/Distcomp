using Application.Dtos;
using AutoMapper;
using Domain.Models;

namespace Application.Profiles;

public class UserDtoProfile : Profile
{
    public UserDtoProfile()
    {
        CreateMap<User, UserGetDto>().ReverseMap();
    }
}
