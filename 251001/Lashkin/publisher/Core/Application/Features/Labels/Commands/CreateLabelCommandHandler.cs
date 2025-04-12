using Application.DTO.Response;
using AutoMapper;
using MediatR;
using Domain.Entities;
using Domain.Interfaces;

namespace Application.Features.Labels.Commands;

public class CreateLabelCommandHandler : IRequestHandler<CreateLabelCommand, LabelResponseTo>
{
    private readonly IUnitOfWork _unitOfWork;
    private readonly IMapper _mapper;

    public CreateLabelCommandHandler(IUnitOfWork unitOfWork, IMapper mapper)
    {
        _unitOfWork = unitOfWork;
        _mapper = mapper;
    }

    public async Task<LabelResponseTo> Handle(CreateLabelCommand request, CancellationToken cancellationToken)
    {
        var label = _mapper.Map<Label>(request.LabelRequestTo);

        _unitOfWork.Label.Create(label);

        await _unitOfWork.SaveChangesAsync(cancellationToken);

        var labelResponseTo = _mapper.Map<LabelResponseTo>(label);

        return labelResponseTo;
    }
}