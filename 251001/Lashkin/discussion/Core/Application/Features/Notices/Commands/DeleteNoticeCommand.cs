using MediatR;

namespace Application.Features.Notices.Commands;

public record DeleteNoticeCommand(long Id) : IRequest;