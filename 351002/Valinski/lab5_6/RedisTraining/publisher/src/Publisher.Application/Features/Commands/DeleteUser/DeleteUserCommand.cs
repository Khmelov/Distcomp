using MediatR;
using Shared.Commons;

namespace Publisher.Application.Features.Commands.DeleteUser;

public class DeleteUserCommand : IRequest<Result>
{
    public long Id { get; set; }
}
