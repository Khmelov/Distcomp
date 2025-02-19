using System;
using Application.Abstractions;
using Domain.Repositories;
using Domain.Shared;
using Microsoft.VisualBasic;

namespace Application.Features.User.Commands;

public class DeleteUserCommandHandler : ICommandHandler<DeleteUserCommand>
{
    private readonly IUserRepository _userRepository;

    public DeleteUserCommandHandler(IUserRepository userRepository)
    {
        _userRepository = userRepository;
    }

    public async Task<Result> Handle(DeleteUserCommand request, CancellationToken cancellationToken)
    {
        var result = await _userRepository.DeleteUser(request.id);

        return result;
    }
}
