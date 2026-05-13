using MediatR;
using Shared.Commons;

namespace Publisher.Application.Features.Commands.DeleteTopic;

public class DeleteTopicCommand : IRequest<Result>
{
    public long Id { get; set; }
}
