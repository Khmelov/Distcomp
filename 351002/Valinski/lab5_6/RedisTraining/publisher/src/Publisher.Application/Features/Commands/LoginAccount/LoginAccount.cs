using MediatR;
using Publisher.Application.Repositories;
using Shared.Commons;

namespace Publisher.Application.Features.Commands.LoginAccount;

public class LoginAccountCommand : IRequest<Result<string>>
{
    public string Login { get; set; } = string.Empty;
    public string Password { get; set; } = string.Empty;
}

public class LoginAccountCommandHandler : IRequestHandler<LoginAccountCommand, Result<string>>
{
    private readonly IUserRepository _userRepository;
    private readonly IJwtGenerator _jwtTokenGenerator;
    
    public LoginAccountCommandHandler(IUserRepository userRepository, IJwtGenerator jwtTokenGenerator)
    {
        _userRepository = userRepository;
        _jwtTokenGenerator = jwtTokenGenerator;
    }

    public async Task<Result<string>> Handle(LoginAccountCommand request, CancellationToken cancellationToken)
    {
        var user = await _userRepository.GetUserByLogin(request.Login);

        if (user == null)
        {
            return Result<string>.Failure("Invalid login attempt.", ErrorType.Unauthorized);
        }
        
        if(user.Password != request.Password)
        {
            return Result<string>.Failure("Invalid login or password", ErrorType.Unauthorized);
        }

        var jwt = _jwtTokenGenerator.GetJwt(user);
        
        return Result<string>.Success(jwt);
    }
}
