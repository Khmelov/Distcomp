using MediatR;
using Publisher.Application.ViewModel;
using Shared.Commons;

namespace Publisher.Application.Features.Commands.UpdateReaction;

public class UpdateReactionCommand : IRequest<Result<ReactionResponseViewModel>>
{
    public long Id { get; set; }
    public long TopicId { get; set; }
    public string? Country { get; set; } 
    public string? Content { get; set; } 
}
