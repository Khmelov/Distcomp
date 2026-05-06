using MediatR;
using Publisher.Application.ViewModel;
using Shared.Commons;

namespace Publisher.Application.Features.Commands.CreateTopic;

public class CreateTopicCommand : IRequest<Result<TopicResponseViewModel>>
{
    public long UserId { get; set; }
    
    public string Title { get; set; } = string.Empty;
    
    public string Content { get; set; } = string.Empty;
}
