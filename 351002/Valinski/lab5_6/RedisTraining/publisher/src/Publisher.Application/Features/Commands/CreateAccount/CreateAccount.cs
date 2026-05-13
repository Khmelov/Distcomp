using AutoMapper;
using MediatR;
using Publisher.Application.Repositories;
using Publisher.Application.ViewModel;
using Publisher.Domain.Models;
using Shared.Commons;

namespace Publisher.Application.Features.Commands.CreateAccount;

public class CreateAccountCommand : IRequest<Result<AccountResponseViewModel>>
{
    public required string Login { get; set; }
    public required string Password { get; set; }
    public required string Firstname { get; set; }
    public required string Lastname { get; set; }
    public required string Role { get; set; }
}

public class CreateAccountCommandHandler : IRequestHandler<CreateAccountCommand, Result<AccountResponseViewModel>>
{
    private readonly IUserRepository _userRepository;
    private readonly IMapper _mapper;

    public CreateAccountCommandHandler(IUserRepository userRepository, IMapper mapper)
    {
        _userRepository = userRepository;
        _mapper = mapper;
    }

    public async Task<Result<AccountResponseViewModel>> Handle(CreateAccountCommand request, CancellationToken cancellationToken)
    {
        var account = await _userRepository.GetUserByLogin(request.Login);
        if (account != null)
        {
            return Result<AccountResponseViewModel>.Failure("Account with this login already exists", ErrorType.Conflict);
        }

        var accountToCreate = new User()
        {
            Firstname = request.Firstname,
            Lastname = request.Lastname,
            Login = request.Login,
            Password = request.Password,
            Role = request.Role
        };
        
        await _userRepository.AddAsync(accountToCreate);
        
        return Result<AccountResponseViewModel>.Success(_mapper.Map<AccountResponseViewModel>(accountToCreate));
    }
}
