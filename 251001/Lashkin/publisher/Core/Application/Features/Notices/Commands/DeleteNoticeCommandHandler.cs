using Application.Exceptions;
using AutoMapper;
using Domain.Interfaces;
using MediatR;

namespace Application.Features.Notices.Commands;

public class DeleteNoticeCommandHandler : IRequestHandler<DeleteNoticeCommand>
{
    private readonly IUnitOfWork _unitOfWork;
    private readonly IMapper _mapper;

    public DeleteNoticeCommandHandler(IUnitOfWork unitOfWork, IMapper mapper)
    {
        _unitOfWork = unitOfWork;
        _mapper = mapper;
    }

    public async Task Handle(DeleteNoticeCommand request, CancellationToken cancellationToken)
    {
        var notice = await _unitOfWork.Notice.FindByIdAsync(request.Id, cancellationToken);

        if (notice == null)
        {
            throw new NotFoundException(string.Format(ExceptionMessages.NoticeNotFound, request.Id));
        }

        _unitOfWork.Notice.Delete(notice);

        await _unitOfWork.SaveChangesAsync(cancellationToken);
    }
}