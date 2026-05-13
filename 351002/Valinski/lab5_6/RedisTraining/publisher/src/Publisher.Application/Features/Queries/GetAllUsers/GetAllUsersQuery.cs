using MediatR;
using Publisher.Application.ViewModel;
using Shared.Commons;

namespace Publisher.Application.Features.Queries.GetAllUsers;

public class GetAllUsersQuery : IRequest<Result<List<UserResponseViewModel>>>
{
    
}
