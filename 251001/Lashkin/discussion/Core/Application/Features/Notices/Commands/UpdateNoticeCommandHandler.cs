using Application.DTO.Response;
using Application.Exceptions;
using AutoMapper;
using Domain.Entities;
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
        var notice = await _unitOfWork.Notice.GetByIdAsync(request.Id, cancellationToken);

        if (notice == null)
        {
            throw new NotFoundException(string.Format(ExceptionMessages.NoticeNotFound, request.Id));
        }

        notice = _mapper.Map<Notice>(request.NoticeRequestTo);
        notice.Id = request.Id;
        
        notice = await _unitOfWork.Notice.UpdateAsync(request.Id, notice, cancellationToken);
        
        var noticeResponseTo = _mapper.Map<NoticeResponseTo>(notice);

        return noticeResponseTo;
    }
}