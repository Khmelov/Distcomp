using System.Text.Json;
using AutoMapper;
using MediatR;
using Publisher.Application.Repositories;
using Publisher.Application.ViewModel;
using Shared.Commons;
using StackExchange.Redis;

namespace Publisher.Application.Features.Commands.UpdateUser;

public class UpdateUserCommandHandler : IRequestHandler<UpdateUser.UpdateUserCommand, Result<UserResponseViewModel>>
{
    private readonly IUserRepository _userRepository;
    private readonly IDatabase _redis;
    private readonly IMapper _mapper;

    public UpdateUserCommandHandler(IUserRepository userRepository, IConnectionMultiplexer conn, IMapper mapper)
    {
        _userRepository = userRepository;
        _mapper = mapper;
        _redis = conn.GetDatabase();
    }

    public async Task<Result<UserResponseViewModel>> Handle(UpdateUser.UpdateUserCommand request, CancellationToken cancellationToken)
    {
        var userFromRepo = await _userRepository.GetByIdAsync(request.Id);
        if (userFromRepo == null)
        {
            return Result<UserResponseViewModel>.Failure("User not found", ErrorType.NotFound);
        }
        
        userFromRepo.Firstname = request.Firstname;
        userFromRepo.Lastname = request.Lastname;
        userFromRepo.Login = request.Login;
        userFromRepo.Password = request.Password;

        var res = await _userRepository.UpdateAsync(userFromRepo);

        var redisKey = $"users:{res.Id}";
        var userResponseViewModel = _mapper.Map<UserResponseViewModel>(res);
        
        await _redis.StringSetAsync(redisKey, JsonSerializer.SerializeToUtf8Bytes(userResponseViewModel));

        return Result<UserResponseViewModel>.Success(userResponseViewModel);
    }
}
