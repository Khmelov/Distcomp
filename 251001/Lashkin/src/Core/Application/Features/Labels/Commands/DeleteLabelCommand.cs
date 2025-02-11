using Application.DTO.Request;
using Application.DTO.Response;
using MediatR;

namespace Application.Features.Labels.Commands;

public record DeleteLabelCommand(long Id) : IRequest<LabelResponseTo>;