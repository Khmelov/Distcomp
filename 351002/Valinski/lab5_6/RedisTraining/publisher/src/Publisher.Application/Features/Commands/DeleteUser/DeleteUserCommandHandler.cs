using MediatR;
using Publisher.Application.Repositories;
using Shared.Commons;
using StackExchange.Redis;

namespace Publisher.Application.Features.Commands.DeleteUser;

public class DeleteUserCommandHandler : IRequestHandler<DeleteUserCommand, Result>
{
    private readonly IUserRepository _userRepository;
    private readonly IDatabase _redis;

    public DeleteUserCommandHandler(IUserRepository userRepository, IConnectionMultiplexer conn)
    {
        _userRepository = userRepository;
        _redis = conn.GetDatabase();
    }

    public async Task<Result> Handle(DeleteUserCommand request, CancellationToken cancellationToken)
    {
        var userToDelete = await _userRepository.GetByIdAsync(request.Id);

        if (userToDelete == null)
        {
            return Result.Failure("User not found", ErrorType.NotFound);
        }

        await _userRepository.DeleteAsync(userToDelete);

        var cachedUser = await _redis.StringGetAsync($"users:{request.Id}");
        if (cachedUser.HasValue && !cachedUser.IsNull)
        {
            await _redis.KeyDeleteAsync($"users:{request.Id}");
        }

        return Result.Success();
    }
}
