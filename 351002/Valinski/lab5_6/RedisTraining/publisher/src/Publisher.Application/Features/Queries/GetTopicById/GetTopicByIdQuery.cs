using MediatR;
using Publisher.Application.ViewModel;
using Shared.Commons;

namespace Publisher.Application.Features.Queries.GetTopicById;

public class GetTopicByIdQuery : IRequest<Result<TopicResponseViewModel>>
{
    public long Id { get; set; }
}
