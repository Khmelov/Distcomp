using Application.DTO.Response;
using MediatR;

namespace Application.Features.Labels.Queries;

public record ReadLabelQuery(long Id) : IRequest<LabelResponseTo>;