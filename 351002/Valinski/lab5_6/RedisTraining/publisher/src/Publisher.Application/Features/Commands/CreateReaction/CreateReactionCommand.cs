using MediatR;
using Publisher.Application.ViewModel;
using Shared.Commons;

namespace Publisher.Application.Features.Commands.CreateReaction;

public class CreateReactionCommand : IRequest<Result<ReactionResponseViewModel>>
{
    public long TopicId { get; set; }
    public string Country { get; set; } = string.Empty;
    public string Content { get; set; } = string.Empty;
}
