using RV.Views.DTO;

namespace RV.Services.DataProviderServices
{
    public interface IUserDataProvider
    {
        UserDTO CreateUser(UserAddDTO item);
        List<UserDTO> GetUsers();
        UserDTO GetUser(int id);
        UserDTO UpdateUser(UserUpdateDTO item);
        int DeleteUser(int id);
    }
}
