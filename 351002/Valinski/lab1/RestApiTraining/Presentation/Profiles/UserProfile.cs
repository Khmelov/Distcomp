using Application.Dtos;
using AutoMapper;
using Domain.Models;
using Presentation.Contracts.Requests;
using Presentation.Contracts.Responses;

namespace Presentation.Profiles;

public class UserProfile : Profile
{
    public UserProfile()
    {
        CreateMap<UserResponseTo, UserGetDto>().ReverseMap();
        CreateMap<UserRequestTo, UserCreateDto>().ReverseMap();
        CreateMap<UserUpdateRequestTo, UserUpdateDto>().ReverseMap();
    }
}
