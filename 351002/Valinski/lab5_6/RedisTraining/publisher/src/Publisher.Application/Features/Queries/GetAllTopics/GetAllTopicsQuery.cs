using MediatR;
using Publisher.Application.ViewModel;
using Shared.Commons;

namespace Publisher.Application.Features.Queries.GetAllTopics;

public class GetAllTopicsQuery : IRequest<Result<List<TopicResponseViewModel>>>
{
}
