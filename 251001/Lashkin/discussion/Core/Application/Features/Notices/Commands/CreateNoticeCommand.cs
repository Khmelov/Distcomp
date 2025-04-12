using Application.DTO.Request;
using Application.DTO.Response;
using MediatR;

namespace Application.Features.Notices.Commands;

public record CreateNoticeCommand(NoticeRequestTo NoticeRequestTo) : IRequest<NoticeResponseTo>;