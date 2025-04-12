using Application.DTO.Response;
using AutoMapper;
using Domain.Interfaces;
using MediatR;

namespace Application.Features.Notices.Queries;

public class ReadNoticesQueryHandler : IRequestHandler<ReadNoticesQuery, IEnumerable<NoticeResponseTo>>
{
    private readonly IUnitOfWork _unitOfWork;
    private readonly IMapper _mapper;

    public ReadNoticesQueryHandler(IUnitOfWork unitOfWork, IMapper mapper)
    {
        _unitOfWork = unitOfWork;
        _mapper = mapper;
    }

    public async Task<IEnumerable<NoticeResponseTo>> Handle(ReadNoticesQuery request, CancellationToken cancellationToken)
    {
        var notices = await _unitOfWork.Notice.GetAllAsync(cancellationToken);

        var noticesResponseTo = _mapper.Map<IEnumerable<NoticeResponseTo>>(notices);

        return noticesResponseTo;
    }
}