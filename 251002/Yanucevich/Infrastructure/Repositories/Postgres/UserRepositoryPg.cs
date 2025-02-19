using System;
using Azure.Core;
using Domain.Models;
using Domain.Repositories;
using Domain.Shared;
using Infrastructure.Errors;
using Infrastructure.Repositories.Interfaces;
using Infrastructure.Settings;
using Microsoft.Extensions.Options;
using Npgsql;

namespace Infrastructure.Repositories.Postgres;

public class UserRepositoryPg : PgRepository, IUserRepository
{
    public UserRepositoryPg(IOptions<InfrastructureOptions> settings) : base(settings.Value)
    {
    }

    public async Task<Result<UserModel>> CreateUser(UserModel user)
    {
        const string sqlCreateUser = 
        """
           INSERT 
             INTO tbl_user
                  (
                    login
                  , password
                  , firstname
                  , lastname
                  )
           VALUES (
                    @Login
                  , @Password
                  , @Firstname
                  , @Lastname
                  )
        RETURNING id
        """;

        await using var connection = await GetConnection();
        await using var cmd = new NpgsqlCommand(sqlCreateUser, connection);

        cmd.Parameters.AddWithValue("Login", user.Login);
        cmd.Parameters.AddWithValue("Password", user.Password);
        cmd.Parameters.AddWithValue("Firstname", user.Firstname);
        cmd.Parameters.AddWithValue("Lastname", user.Lastname);

        try{
            using var reader = await cmd.ExecuteReaderAsync(System.Data.CommandBehavior.SingleRow);
            if (await reader.ReadAsync()){
                var userId = reader.GetInt64(0);
                return new UserModel{
                    Id = userId,
                    Login = user.Login,
                    Password = user.Password,
                    Firstname = user.Firstname,
                    Lastname = user.Lastname
                };
            }
        }
        catch(NpgsqlException ex) when (ex.SqlState == "23505"){
            return Result.Failure<UserModel>(UserErrors.UserNotUniqueError);
        }
        catch (NpgsqlException ex){
            return Result.Failure<UserModel>(Error.DatabaseError);
        }
        return Result.Failure<UserModel>(Error.Unknown);
    }

    public async Task<Result> DeleteUser(long id)
    {
        const string sqlDeleteUser = 
        """
        DELETE
          FROM tbl_user
         WHERE id = @Id
        """;

        await using var connection = await GetConnection();
        await using var cmd = new NpgsqlCommand(sqlDeleteUser, connection);

        cmd.Parameters.AddWithValue("Id", id);

        int result;
        try{
            result = await cmd.ExecuteNonQueryAsync();
        }
        catch(NpgsqlException ex){
            return Result.Failure(Error.DatabaseError);
        }

        return result == 0 ? Result.Failure(UserErrors.UserNotFoundError) : Result.Success();
    }

    public async Task<Result<UserModel>> GetUserById(long id)
    {
        const string sqlGetUserById = 
        """
        SELECT id
             , login
             , password
             , firstname
             , lastname
          FROM tbl_user
         WHERE id = @Id
        """;

        await using var connection = await GetConnection();

        using var cmd = new NpgsqlCommand(sqlGetUserById, connection);
        cmd.Parameters.AddWithValue("Id", id);

        try{
            var reader = await cmd.ExecuteReaderAsync();

            if (await reader.ReadAsync()){
                var userId = reader.GetInt64(reader.GetOrdinal("id"));
                var userLogin = reader.GetString(reader.GetOrdinal("login"));
                var userPassword = reader.GetString(reader.GetOrdinal("password"));
                var userFirstname = reader.GetString(reader.GetOrdinal("firstname"));
                var userLastname = reader.GetString(reader.GetOrdinal("lastname"));

                return new UserModel{
                    Id = userId,
                    Login = userLogin,
                    Password = userPassword,
                    Firstname = userFirstname,
                    Lastname = userLastname
                };
            }
            else{
                return Result.Failure<UserModel>(UserErrors.UserNotFoundError);
            }
        }
        catch(NpgsqlException ex){
            return Result.Failure<UserModel>(Error.DatabaseError);
        }
    }

    public async Task<Result<IEnumerable<UserModel>>> GetUsers()
    {
        const string sqlGetUsers = 
        """
        SELECT id
             , login
             , password
             , firstname
             , lastname
          FROM tbl_user
        """;

        await using var connection = await GetConnection();

        using var cmd = new NpgsqlCommand(sqlGetUsers, connection);

        try{
            using var reader = await cmd.ExecuteReaderAsync();

            var users = new List<UserModel>();

            while (await reader.ReadAsync()){
                var userId = reader.GetInt64(reader.GetOrdinal("id"));
                var userLogin = reader.GetString(reader.GetOrdinal("login"));
                var userPassword = reader.GetString(reader.GetOrdinal("password"));
                var userFirstname = reader.GetString(reader.GetOrdinal("firstname"));
                var userLastname = reader.GetString(reader.GetOrdinal("lastname"));

                users.Add(new UserModel{
                    Id = userId,
                    Login = userLogin,
                    Password = userPassword,
                    Firstname = userFirstname,
                    Lastname = userLastname
                });
            }
            return users;
        }
        catch(NpgsqlException ex){
            return Result.Failure<IEnumerable<UserModel>>(Error.DatabaseError);
        }
    }

    public async Task<Result<UserModel>> UpdateUser(UserModel user)
    {
        const string sqlUpdateUser = 
        """
           UPDATE tbl_user
              SET login = @Login
                , password = @Password
                , firstname = @Firstname
                , lastname = @Lastname
            WHERE id = @Id
        RETURNING id
        """;

        await using var connection = await GetConnection();
        using var cmd = new NpgsqlCommand(sqlUpdateUser, connection);

        cmd.Parameters.AddWithValue("Id", user.Id);
        cmd.Parameters.AddWithValue("Login", user.Login);
        cmd.Parameters.AddWithValue("Password", user.Password);
        cmd.Parameters.AddWithValue("Firstname", user.Firstname);
        cmd.Parameters.AddWithValue("Lastname", user.Lastname);

        try{
            var returnedId = await cmd.ExecuteScalarAsync();

            if(returnedId != null && (long) returnedId == user.Id){
                return new UserModel{
                    Id = (long)returnedId,
                    Login = user.Login,
                    Password = user.Password,
                    Firstname = user.Firstname,
                    Lastname = user.Lastname
                };
            }
            return Result.Failure<UserModel>(UserErrors.UserNotFoundError);
        }
        catch(NpgsqlException ex) when (ex.SqlState == "23505"){
            return Result.Failure<UserModel>(UserErrors.UserNotUniqueError);
        }
        catch(NpgsqlException ex){
            return Result.Failure<UserModel>(Error.DatabaseError);
        }
    }
}
