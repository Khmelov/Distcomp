using MediatR;
using Publisher.Application.ViewModel;
using Shared.Commons;

namespace Publisher.Application.Features.Commands.CreateLabel;

public class CreateLabelCommand : IRequest<Result<LabelResponseViewModel>>
{
    public string Name { get; set; } = string.Empty;
}
