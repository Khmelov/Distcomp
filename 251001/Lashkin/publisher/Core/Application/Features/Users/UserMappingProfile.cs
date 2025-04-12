using Application.DTO.Request;
using Application.DTO.Response;
using AutoMapper;
using Domain.Entities;

namespace Application.Features.Users;

public class UserMappingProfile : Profile
{
    public UserMappingProfile()
    {
        CreateMap<UserRequestTo, User>();

        CreateMap<User, UserResponseTo>();
    }
}