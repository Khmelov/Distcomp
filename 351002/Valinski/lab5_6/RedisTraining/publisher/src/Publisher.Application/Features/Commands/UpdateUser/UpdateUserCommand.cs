using MediatR;
using Publisher.Application.ViewModel;
using Shared.Commons;

namespace Publisher.Application.Features.Commands.UpdateUser;

public class UpdateUserCommand : IRequest<Result<UserResponseViewModel>>
{
    public long Id { get; set; }
    public string Login { get; set; } = string.Empty;
    public string Firstname { get; set; } = string.Empty;
    public string Lastname { get; set; } = string.Empty;
    public string Password { get; set; } = string.Empty;
}
