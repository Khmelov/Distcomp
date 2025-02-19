using System;
using System.Data.Common;
using Domain.Models;
using Domain.Repositories;
using Domain.Shared;
using Infrastructure.Errors;

namespace Infrastructure.Repositories.InMemory;

public class UserRepositoryInMemory : IUserRepository
{
    private Dictionary<long, UserModel> _users = new();
    private long _id = 0;

    public UserRepositoryInMemory(){
        CreateUser(new UserModel{
            Id = 1000,
            Login = "yanucevich.d@gmail.com",
            Password = "1234",
            Firstname = "Дмитрий",
            Lastname = "Януцевич"
        }).Wait();
    }

    public Task<Result<UserModel>> CreateUser(UserModel user)
    {
        if(_users.Values.Where(u => u.Login == user.Login).Any()){
            return Task.FromResult(Result.Failure<UserModel>(UserErrors.UserNotUniqueError));
        }   

        _users.Add(_id, new UserModel{
            Id = _id, 
            Login = user.Login, 
            Firstname = user.Firstname, 
            Lastname = user.Lastname, 
            Password = user.Password}
        );
        return Task.FromResult(Result.Success(_users[_id++]));
    }

    public Task<Result> DeleteUser(long id)
    {
        return Task.FromResult(
            _users.Remove(id) 
                ? Result.Success()
                : Result.Failure(UserErrors.UserNotFoundError)
        );
    }

    public Task<Result<UserModel>> GetUserById(long id)
    {
        return Task.FromResult(
            _users.TryGetValue(id, out var result)
                ? Result.Success(result)
                : Result.Failure<UserModel>(UserErrors.UserNotFoundError)
        );
    }

    public Task<Result<IEnumerable<UserModel>>> GetUsers()
    {
        return Task.FromResult(Result.Success<IEnumerable<UserModel>>(_users.Values));
    }

    public Task<Result<UserModel>> UpdateUser(UserModel user)
    {
        if(!_users.ContainsKey(user.Id)){
            return Task.FromResult(Result.Failure<UserModel>(
                UserErrors.UserNotFoundError
            ));
        }

        if(_users.Values.Where(u => u.Login == user.Login && u.Id != user.Id).Any()){
            return Task.FromResult(Result.Failure<UserModel>(
                UserErrors.UserNotUniqueError
            ));
        }

        _users[user.Id] = user;
        return Task.FromResult(Result.Success(_users[user.Id]));
    }
}
