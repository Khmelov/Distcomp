using Application.DTO.Response;
using MediatR;

namespace Application.Features.Labels.Queries;

public record ReadLabelsQuery() : IRequest<IEnumerable<LabelResponseTo>>;