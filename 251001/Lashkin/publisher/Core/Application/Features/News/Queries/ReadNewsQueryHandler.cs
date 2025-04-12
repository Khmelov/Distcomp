using Application.DTO.Response;
using Application.Exceptions;
using AutoMapper;
using Domain.Interfaces;
using MediatR;

namespace Application.Features.News.Queries;

public class ReadNewsQueryHandler : IRequestHandler<ReadNewsQuery, NewsResponseTo>
{
    private readonly IUnitOfWork _unitOfWork;
    private readonly IMapper _mapper;

    public ReadNewsQueryHandler(IUnitOfWork unitOfWork, IMapper mapper)
    {
        _unitOfWork = unitOfWork;
        _mapper = mapper;
    }

    public async Task<NewsResponseTo> Handle(ReadNewsQuery request, CancellationToken cancellationToken)
    {
        var news = await _unitOfWork.News.FindByIdAsync(request.Id, cancellationToken);

        if (news == null)
        {
            throw new NotFoundException(string.Format(ExceptionMessages.NewsNotFound, request.Id));
        }

        var newsResponseTo = _mapper.Map<NewsResponseTo>(news);

        return newsResponseTo;
    }
}