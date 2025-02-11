using Application.DTO.Response;
using AutoMapper;
using Domain.Repositories;
using MediatR;
using Domain.Entities;

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
        var notice = _mapper.Map<Notice>(request.NoticeRequestTo);
        
        _unitOfWork.Notice.Create(notice);

        await _unitOfWork.SaveChangesAsync();
        
        var noticeResponseTo = _mapper.Map<NoticeResponseTo>(notice);
        
        return noticeResponseTo;
    }
}