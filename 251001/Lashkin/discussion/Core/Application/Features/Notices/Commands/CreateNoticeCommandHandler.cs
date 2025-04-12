using Application.DTO.Response;
using AutoMapper;
using MediatR;
using Domain.Entities;
using Domain.Interfaces;

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
        // var news = await _unitOfWork.News.FindByCondition(news => news.Id == request.NoticeRequestTo.NewsId, false).SingleOrDefaultAsync(cancellationToken);
        //
        // if (news == null)
        // {
        //     throw new NotFoundException(string.Format(ExceptionMessages.NewsNotFound, request.NoticeRequestTo.NewsId));
        // }
        
        var notice = _mapper.Map<Notice>(request.NoticeRequestTo);

        notice.Id = await _unitOfWork.Sequence.GetNextIdAsync("notices");
        
        await _unitOfWork.Notice.AddAsync(notice, cancellationToken);
        
        var noticeResponseTo = _mapper.Map<NoticeResponseTo>(notice);

        return noticeResponseTo;
    }
}