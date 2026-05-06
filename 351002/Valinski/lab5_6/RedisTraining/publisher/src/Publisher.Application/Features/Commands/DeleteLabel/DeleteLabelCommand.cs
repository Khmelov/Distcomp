using MediatR;
using Shared.Commons;

namespace Publisher.Application.Features.Commands.DeleteLabel;

public class DeleteLabelCommand : IRequest<Result>
{
    public long Id { get; set; }
}
