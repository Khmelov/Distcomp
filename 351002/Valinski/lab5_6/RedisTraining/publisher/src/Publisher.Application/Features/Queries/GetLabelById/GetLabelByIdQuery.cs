using MediatR;
using Publisher.Application.ViewModel;
using Shared.Commons;

namespace Publisher.Application.Features.Queries.GetLabelById;

public class GetLabelByIdQuery : IRequest<Result<LabelResponseViewModel>>
{
    public long Id { get; set; }
}
