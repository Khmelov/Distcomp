using System;
using Application.Abstractions;
using Domain.Mappers;
using Domain.Models;
using Domain.Projections;
using Domain.Repositories;
using Domain.Shared;

namespace Application.Features.User.Commands;

public class UpdateUserCommandHandler : ICommandHandler<UpdateUserCommand, UserProjection>
{
    private readonly IUserRepository _userRepository;

    public UpdateUserCommandHandler(IUserRepository userRepository)
    {
        _userRepository = userRepository;
    }

    public async Task<Result<UserProjection>> Handle(UpdateUserCommand request, CancellationToken cancellationToken)
    {
        var user = new UserModel{
            Id = request.id, 
            Login = request.login,
            Password = request.password,
            Firstname = request.firstname,
            Lastname = request.lastname
        };

        var resultUser = await _userRepository.UpdateUser(user);
    
        if(!resultUser.IsSuccess){
            return Result.Failure<UserProjection>(resultUser.Error);
        }

        return resultUser.Value.ToUserProjection();
    }
}
