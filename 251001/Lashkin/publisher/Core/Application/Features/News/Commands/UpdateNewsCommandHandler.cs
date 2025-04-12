using Application.DTO.Response;
using Application.Exceptions;
using AutoMapper;
using Domain.Interfaces;
using MediatR;
using Microsoft.EntityFrameworkCore;

namespace Application.Features.News.Commands;

public class UpdateNewsCommandHandler : IRequestHandler<UpdateNewsCommand, NewsResponseTo>
{
    private readonly IUnitOfWork _unitOfWork;
    private readonly IMapper _mapper;

    public UpdateNewsCommandHandler(IUnitOfWork unitOfWork, IMapper mapper)
    {
        _unitOfWork = unitOfWork;
        _mapper = mapper;
    }

    public async Task<NewsResponseTo> Handle(UpdateNewsCommand request, CancellationToken cancellationToken)
    {
        var news = await _unitOfWork.News.FindByIdAsync(request.Id, cancellationToken);

        if (news == null)
        {
            throw new NotFoundException(string.Format(ExceptionMessages.NewsNotFound, request.Id));
        }

        if (await _unitOfWork.News.FindByCondition(entity => entity.Title == request.NewsRequestTo.Title, false).SingleOrDefaultAsync(cancellationToken) != null)
        {
            throw new AlreadyExistsException(string.Format(ExceptionMessages.NewsAlreadyExists, news.Title));
        }

        _mapper.Map(request.NewsRequestTo, news);

        await _unitOfWork.SaveChangesAsync(cancellationToken);

        var newResponseTo = _mapper.Map<NewsResponseTo>(news);

        return newResponseTo;
    }
}