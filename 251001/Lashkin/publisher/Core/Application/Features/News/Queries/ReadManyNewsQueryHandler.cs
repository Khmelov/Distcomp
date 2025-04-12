using Application.DTO.Response;
using AutoMapper;
using Domain.Interfaces;
using MediatR;
using Microsoft.EntityFrameworkCore;

namespace Application.Features.News.Queries;

public class ReadManyNewsQueryHandler : IRequestHandler<ReadManyNewsQuery, IEnumerable<NewsResponseTo>>
{
    private readonly IUnitOfWork _unitOfWork;
    private readonly IMapper _mapper;

    public ReadManyNewsQueryHandler(IUnitOfWork unitOfWork, IMapper mapper)
    {
        _unitOfWork = unitOfWork;
        _mapper = mapper;
    }

    public async Task<IEnumerable<NewsResponseTo>> Handle(ReadManyNewsQuery request, CancellationToken cancellationToken)
    {
        var news = await _unitOfWork.News.FindAll(false).ToListAsync(cancellationToken);

        var newsResponseTo = _mapper.Map<IEnumerable<NewsResponseTo>>(news);

        return newsResponseTo;
    }
}