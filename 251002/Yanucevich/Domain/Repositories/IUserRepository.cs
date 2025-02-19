using System;
using Domain.Models;
using Domain.Shared;

namespace Domain.Repositories;

public interface IUserRepository
{
    Task<Result<UserModel>> CreateUser(UserModel user);
    Task<Result<IEnumerable<UserModel>>> GetUsers();
    Task<Result<UserModel>> GetUserById(long id);
    Task<Result<UserModel>> UpdateUser(UserModel user);
    Task<Result> DeleteUser(long id);
}
