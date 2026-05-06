using System.Text.Json;
using AutoMapper;
using MediatR;
using Publisher.Application.Repositories;
using Publisher.Application.ViewModel;
using Publisher.Domain.Models;
using Shared.Commons;
using StackExchange.Redis;

namespace Publisher.Application.Features.Commands.CreateUser;

public class CreateUserCommandHandler : IRequestHandler<CreateUserCommand, Result<UserResponseViewModel>>
{
    private readonly IUserRepository _userRepository;
    private readonly IDatabase _redis;
    private readonly IMapper _mapper;
        
    public CreateUserCommandHandler(IUserRepository userRepository, IConnectionMultiplexer conn, IMapper mapper)
    {
        _userRepository = userRepository;
        _redis = conn.GetDatabase();
        _mapper = mapper;
    }
    
    public async Task<Result<UserResponseViewModel>> Handle(CreateUserCommand request, CancellationToken cancellationToken)
    {
        var user = await _userRepository.GetUserByLogin(request.Login);
        if (user != null)
        {
            return Result<UserResponseViewModel>.Failure("User with this username already exists", ErrorType.Conflict);
        }
        
        var userToAdd = new User()
        {
            Firstname = request.Firstname,
            Login = request.Login,
            Lastname = request.Lastname,
            Password = request.Password
        };

        await _userRepository.AddAsync(userToAdd);

        var userResponse = _mapper.Map<UserResponseViewModel>(userToAdd);
        await _redis.StringSetAsync($"users:{userToAdd.Id}", JsonSerializer.SerializeToUtf8Bytes(userResponse));
        
        return Result<UserResponseViewModel>.Success(userResponse);
    }
}
