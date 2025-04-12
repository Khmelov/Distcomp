using Application.DTO.Response;
using MediatR;

namespace Application.Features.Notices.Queries;

public record ReadNoticeQuery(long Id) : IRequest<NoticeResponseTo>;