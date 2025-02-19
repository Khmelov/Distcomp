using System;
using Application.Abstractions;
using Domain.Mappers;
using Domain.Models;
using Domain.Projections;
using Domain.Repositories;
using Domain.Shared;

namespace Application.Features.User.Commands;

public class CreateUserCommandHandler : ICommandHandler<CreateUserCommand, UserProjection>
{
    private readonly IUserRepository _userRepository;

    public CreateUserCommandHandler(IUserRepository userRepository)
    {
        _userRepository = userRepository;
    }

    public async Task<Result<UserProjection>> Handle(CreateUserCommand request, CancellationToken cancellationToken)
    {

        var user = new UserModel{
                Login = request.login,
                Password = request.password,
                Firstname = request.firstname,
                Lastname = request.lastname
        };
        var resultUser = await _userRepository.CreateUser(user);

        if (!resultUser.IsSuccess){
            return Result.Failure<UserProjection>(resultUser.Error);
        }

        return Result.Success(resultUser.Value.ToUserProjection());
    }
}
