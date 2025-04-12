using Application.DTO.Response;
using MediatR;

namespace Application.Features.Notices.Queries;

public record ReadNoticesQuery() : IRequest<IEnumerable<NoticeResponseTo>>;