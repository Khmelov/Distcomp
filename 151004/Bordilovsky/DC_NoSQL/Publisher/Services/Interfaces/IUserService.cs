using DC_REST.DTOs.Request;
using DC_REST.DTOs.Response;

namespace DC_REST.Services.Interfaces
{
    public interface IUserService
	{
		UserResponseTo CreateUser(UserRequestTo userRequestDto);
		UserResponseTo GetUserById(int id);
		List<UserResponseTo> GetAllUsers();
		UserResponseTo UpdateUser(int id, UserRequestTo userRequestDto);
		bool DeleteUser(int id);
	}
}
