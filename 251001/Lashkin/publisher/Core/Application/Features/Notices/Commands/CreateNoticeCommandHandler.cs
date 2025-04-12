using Application.DTO.Response;
using Application.Exceptions;
using AutoMapper;
using MediatR;
using Domain.Entities;
using Domain.Interfaces;
using Microsoft.EntityFrameworkCore;

namespace Application.Features.Notices.Commands;

public class CreateNoticeCommandHandler : IRequestHandler<CreateNoticeCommand, NoticeResponseTo>
{
    private readonly IUnitOfWork _unitOfWork;
    private readonly IMapper _mapper;

    public CreateNoticeCommandHandler(IUnitOfWork unitOfWork, IMapper mapper)
    {
        _unitOfWork = unitOfWork;
        _mapper = mapper;
    }

    public async Task<NoticeResponseTo> Handle(CreateNoticeCommand request, CancellationToken cancellationToken)
    {
        var news = await _unitOfWork.News.FindByCondition(news => news.Id == request.NoticeRequestTo.NewsId, false).SingleOrDefaultAsync(cancellationToken);

        if (news == null)
        {
            throw new NotFoundException(string.Format(ExceptionMessages.NewsNotFound, request.NoticeRequestTo.NewsId));
        }
        
        var notice = _mapper.Map<Notice>(request.NoticeRequestTo);

        _unitOfWork.Notice.Create(notice);

        await _unitOfWork.SaveChangesAsync(cancellationToken);

        var noticeResponseTo = _mapper.Map<NoticeResponseTo>(notice);

        return noticeResponseTo;
    }
}