using Application.Exceptions;
using AutoMapper;
using Domain.Interfaces;
using MediatR;

namespace Application.Features.Notices.Commands;

public class DeleteNoticeCommandHandler : IRequestHandler<DeleteNoticeCommand>
{
    private readonly IUnitOfWork _unitOfWork;

    public DeleteNoticeCommandHandler(IUnitOfWork unitOfWork)
    {
        _unitOfWork = unitOfWork;
    }

    public async Task Handle(DeleteNoticeCommand request, CancellationToken cancellationToken)
    {
        var notice = await _unitOfWork.Notice.GetByIdAsync(request.Id, cancellationToken);

        if (notice == null)
        {
            throw new NotFoundException(string.Format(ExceptionMessages.NoticeNotFound, request.Id));
        }

        await _unitOfWork.Notice.DeleteAsync(request.Id, cancellationToken);
    }
}