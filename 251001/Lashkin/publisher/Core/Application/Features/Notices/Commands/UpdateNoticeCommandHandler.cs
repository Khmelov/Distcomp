using Application.DTO.Response;
using Application.Exceptions;
using AutoMapper;
using Domain.Interfaces;
using MediatR;

namespace Application.Features.Notices.Commands;

public class UpdateNoticeCommandHandler : IRequestHandler<UpdateNoticeCommand, NoticeResponseTo>
{
    private readonly IUnitOfWork _unitOfWork;
    private readonly IMapper _mapper;

    public UpdateNoticeCommandHandler(IUnitOfWork unitOfWork, IMapper mapper)
    {
        _unitOfWork = unitOfWork;
        _mapper = mapper;
    }

    public async Task<NoticeResponseTo> Handle(UpdateNoticeCommand request, CancellationToken cancellationToken)
    {
        var notice = await _unitOfWork.Notice.FindByIdAsync(request.Id, cancellationToken);

        if (notice == null)
        {
            throw new NotFoundException(string.Format(ExceptionMessages.NoticeNotFound, request.Id));
        }

        _mapper.Map(request.NoticeRequestTo, notice);

        await _unitOfWork.SaveChangesAsync(cancellationToken);

        var noticeResponseTo = _mapper.Map<NoticeResponseTo>(notice);

        return noticeResponseTo;
    }
}