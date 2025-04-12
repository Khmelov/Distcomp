using Application.Exceptions;
using AutoMapper;
using Domain.Interfaces;
using MediatR;

namespace Application.Features.News.Commands;

public class DeleteNewsCommandHandler : IRequestHandler<DeleteNewsCommand>
{
    private readonly IUnitOfWork _unitOfWork;
    private readonly IMapper _mapper;

    public DeleteNewsCommandHandler(IUnitOfWork unitOfWork, IMapper mapper)
    {
        _unitOfWork = unitOfWork;
        _mapper = mapper;
    }

    public async Task Handle(DeleteNewsCommand request, CancellationToken cancellationToken)
    {
        var news = await _unitOfWork.News.FindByIdAsync(request.Id, cancellationToken);

        if (news == null)
        {
            throw new NotFoundException(string.Format(ExceptionMessages.NewsNotFound, request.Id));
        }

        _unitOfWork.News.Delete(news);

        await _unitOfWork.SaveChangesAsync(cancellationToken);
    }
}