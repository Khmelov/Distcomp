using MediatR;
using Publisher.Application.ViewModel;
using Shared.Commons;

namespace Publisher.Application.Features.Commands.UpdateLabel;

public class UpdateLabelCommand : IRequest<Result<LabelResponseViewModel>>
{
    public long Id { get; set; }
    public string Name { get; set; } = string.Empty;
}
