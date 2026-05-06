using MediatR;
using Shared.Commons;

namespace Publisher.Application.Features.Commands.DeleteReaction;

public class DeleteReactionCommand : IRequest<Result>
{
    public long Id { get; set; }
}
