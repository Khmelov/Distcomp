using Application.DTO.Response;
using Application.Exceptions;
using AutoMapper;
using Domain.Repositories;
using MediatR;
using Microsoft.EntityFrameworkCore;

namespace Application.Features.News.Commands;

public class CreateNewsCommandHandler : IRequestHandler<CreateNewsCommand, NewsResponseTo>
{
    private readonly IUnitOfWork _unitOfWork;
    private readonly IMapper _mapper;

    public CreateNewsCommandHandler(IUnitOfWork unitOfWork, IMapper mapper)
    {
        _unitOfWork = unitOfWork;
        _mapper = mapper;
    }
    
    public async Task<NewsResponseTo> Handle(CreateNewsCommand request, CancellationToken cancellationToken)
    {
        var news = await _unitOfWork.News.FindByCondition(news => news.Title == request.NewsRequestTo.Title, false).SingleOrDefaultAsync(cancellationToken);

        if (news != null)
        {
            throw new AlreadyExistsException(string.Format(ExceptionMessages.NewsAlreadyExists, news.Title));
        }
        
        news = _mapper.Map<Domain.Entities.News>(request.NewsRequestTo);
        
        _unitOfWork.News.Create(news);

        await _unitOfWork.SaveChangesAsync();
        
        var newsResponseTo = _mapper.Map<NewsResponseTo>(news);
        
        return newsResponseTo;
    }
}