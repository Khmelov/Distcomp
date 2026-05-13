using System.Text.Json;
using AutoMapper;
using MediatR;
using Publisher.Application.Repositories;
using Publisher.Application.ViewModel;
using Shared.Commons;
using StackExchange.Redis;

namespace Publisher.Application.Features.Queries.GetAllUsers;

public class GetAllUsersQueryHandler : IRequestHandler<GetAllUsersQuery, Result<List<UserResponseViewModel>>>
{
    private readonly IMapper _mapper;
    private readonly IUserRepository _userRepository;
    private readonly IDatabase _database;

    public GetAllUsersQueryHandler(IConnectionMultiplexer conn, IUserRepository userRepository, IMapper mapper)
    {
        _userRepository = userRepository;
        _mapper = mapper;
        _database = conn.GetDatabase();
    }

    public async Task<Result<List<UserResponseViewModel>>> Handle(GetAllUsersQuery request, CancellationToken cancellationToken)
    {
        var cache = _database.StringGet("users");
        if (cache.HasValue)
        {
            var value = JsonSerializer.Deserialize<List<UserResponseViewModel>>(cache.ToString());
            
            return Result<List<UserResponseViewModel>>.Success(value!);
        }

        var tempResult = await _userRepository.GetAllAsync();
        
        return Result<List<UserResponseViewModel>>.Success(_mapper.Map<List<UserResponseViewModel>>(tempResult));
    }
}
