using MediatR;
using Publisher.Application.ViewModel;
using Publisher.Domain.Models;
using Shared.Commons;

namespace Publisher.Application.Features.Queries.GetUserById;

public class GetUserByIdQuery : IRequest<Result<UserResponseViewModel>>
{
    public long Id { get; set; }
}
