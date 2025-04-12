using Application.DTO.Response;
using Application.Exceptions;
using AutoMapper;
using Domain.Interfaces;
using MediatR;

namespace Application.Features.Labels.Queries;

public class ReadLabelQueryHandler : IRequestHandler<ReadLabelQuery, LabelResponseTo>
{
    private readonly IUnitOfWork _unitOfWork;
    private readonly IMapper _mapper;

    public ReadLabelQueryHandler(IUnitOfWork unitOfWork, IMapper mapper)
    {
        _unitOfWork = unitOfWork;
        _mapper = mapper;
    }

    public async Task<LabelResponseTo> Handle(ReadLabelQuery request, CancellationToken cancellationToken)
    {
        var label = await _unitOfWork.Label.FindByIdAsync(request.Id, cancellationToken);

        if (label == null)
        {
            throw new NotFoundException(string.Format(ExceptionMessages.LabelNotFound, request.Id));
        }

        var labelResponseTo = _mapper.Map<LabelResponseTo>(label);

        return labelResponseTo;
    }
}