using MediatR;
using Publisher.Application.ViewModel;
using Shared.Commons;

namespace Publisher.Application.Features.Queries.GetAllLabels;

public class GetAllLabelsQuery : IRequest<Result<List<LabelResponseViewModel>>>
{
}

