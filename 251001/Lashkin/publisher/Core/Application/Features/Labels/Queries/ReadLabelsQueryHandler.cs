using Application.DTO.Response;
using AutoMapper;
using Domain.Interfaces;
using MediatR;
using Microsoft.EntityFrameworkCore;

namespace Application.Features.Labels.Queries;

public class ReadLabelsQueryHandler : IRequestHandler<ReadLabelsQuery, IEnumerable<LabelResponseTo>>
{
    private readonly IUnitOfWork _unitOfWork;
    private readonly IMapper _mapper;

    public ReadLabelsQueryHandler(IUnitOfWork unitOfWork, IMapper mapper)
    {
        _unitOfWork = unitOfWork;
        _mapper = mapper;
    }

    public async Task<IEnumerable<LabelResponseTo>> Handle(ReadLabelsQuery request, CancellationToken cancellationToken)
    {
        var labels = await _unitOfWork.Label.FindAll(false).ToListAsync(cancellationToken);

        var labelsResponseTo = _mapper.Map<IEnumerable<LabelResponseTo>>(labels);

        return labelsResponseTo;
    }
}