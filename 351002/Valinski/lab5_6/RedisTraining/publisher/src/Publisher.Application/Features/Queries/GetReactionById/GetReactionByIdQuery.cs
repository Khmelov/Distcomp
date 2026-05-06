using MediatR;
using Publisher.Application.ViewModel;
using Shared.Commons;

namespace Publisher.Application.Features.Queries.GetReactionById;

public class GetReactionByIdQuery : IRequest<Result<ReactionResponseViewModel>>
{
    public long Id { get; set; }
}
