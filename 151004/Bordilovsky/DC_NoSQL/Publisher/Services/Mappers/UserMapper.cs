using AutoMapper;
using DC_REST.DTOs.Request;
using DC_REST.DTOs.Response;
using DC_REST.Entities;

namespace DC_REST.Services.Mappers
{
	public class UserMapper : Profile
	{
		public UserMapper() 
		{
			CreateMap<User, UserResponseTo>();
			CreateMap<UserRequestTo, User>();
		}
	}
}
