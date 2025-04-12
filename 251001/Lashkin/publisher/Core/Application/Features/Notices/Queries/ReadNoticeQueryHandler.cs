using Application.DTO.Response;
using Application.Exceptions;
using AutoMapper;
using Domain.Interfaces;
using MediatR;

namespace Application.Features.Notices.Queries;

public class ReadNoticeQueryHandler : IRequestHandler<ReadNoticeQuery, NoticeResponseTo>
{
    private readonly IUnitOfWork _unitOfWork;
    private readonly IMapper _mapper;

    public ReadNoticeQueryHandler(IUnitOfWork unitOfWork, IMapper mapper)
    {
        _unitOfWork = unitOfWork;
        _mapper = mapper;
    }

    public async Task<NoticeResponseTo> Handle(ReadNoticeQuery request, CancellationToken cancellationToken)
    {
        var notice = await _unitOfWork.Notice.FindByIdAsync(request.Id, cancellationToken);

        if (notice == null)
        {
            throw new NotFoundException(string.Format(ExceptionMessages.NoticeNotFound, request.Id));
        }

        var noticeResponseTo = _mapper.Map<NoticeResponseTo>(notice);

        return noticeResponseTo;
    }
}