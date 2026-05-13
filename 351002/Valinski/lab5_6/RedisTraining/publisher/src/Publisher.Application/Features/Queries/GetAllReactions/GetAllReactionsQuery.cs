using MediatR;
using Publisher.Application.ViewModel;
using Shared.Commons;

namespace Publisher.Application.Features.Queries.GetAllReactions;

public record GetAllReactionsQuery : IRequest<Result<List<ReactionResponseViewModel>>>
{
    
}
