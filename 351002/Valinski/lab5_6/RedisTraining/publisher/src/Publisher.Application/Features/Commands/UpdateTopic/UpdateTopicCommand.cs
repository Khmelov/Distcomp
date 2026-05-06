using MediatR;
using Publisher.Application.ViewModel;
using Shared.Commons;

namespace Publisher.Application.Features.Commands.UpdateTopic;

public class UpdateTopicCommand : IRequest<Result<TopicResponseViewModel>>
{
    public long Id { get; set; }
    
    public string Title { get; set; } = string.Empty;
    
    public string Content { get; set; } = string.Empty;
}
