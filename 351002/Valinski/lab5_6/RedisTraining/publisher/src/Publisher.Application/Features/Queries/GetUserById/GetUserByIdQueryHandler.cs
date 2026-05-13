using System.Text.Json;
using AutoMapper;
using MediatR;
using Publisher.Application.Repositories;
using Publisher.Application.ViewModel;
using Shared.Commons;
using StackExchange.Redis;

namespace Publisher.Application.Features.Queries.GetUserById;

public class GetUserByIdQueryHandler : IRequestHandler<GetUserByIdQuery, Result<UserResponseViewModel>>
{
    private readonly IUserRepository _userRepository;
    private readonly IDatabase _redis;
    private readonly IMapper _mapper;
        
    public GetUserByIdQueryHandler(IUserRepository userRepository, IConnectionMultiplexer conn, IMapper mapper)
    {
        _userRepository = userRepository;
        _redis = conn.GetDatabase();
        _mapper = mapper;
    }

    public async Task<Result<UserResponseViewModel>> Handle(GetUserByIdQuery request, CancellationToken cancellationToken)
    {
        var cachedUser = await _redis.StringGetAsync($"users:{request.Id}");
        if (cachedUser.HasValue && !cachedUser.IsNull)
        {
            var cachedResult = JsonSerializer.Deserialize<UserResponseViewModel>(cachedUser.ToString())!;
            return Result<UserResponseViewModel>.Success(cachedResult);
        }
        
        var user = await _userRepository.GetByIdAsync(request.Id);
        if (user == null)
        {
            return Result<UserResponseViewModel>.Failure("User not found");
        }

        await _redis.StringSetAsync($"users:{request.Id}", JsonSerializer.SerializeToUtf8Bytes(user));
        
        return Result<UserResponseViewModel>.Success(_mapper.Map<UserResponseViewModel>(user));
    }
}
