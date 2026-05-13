using AutoMapper;
using MediatR;
using Publisher.Application.Repositories;
using Publisher.Application.ViewModel;
using Shared.Commons;
using StackExchange.Redis;

namespace Publisher.Application.Features.Commands.UpdateLabel;

public class UpdateLabelCommandHandler : IRequestHandler<UpdateLabelCommand, Result<LabelResponseViewModel>>
{
    private readonly ILabelRepository _labelRepository;
    private readonly IDatabase _redis;
    private readonly IMapper _mapper;

    public UpdateLabelCommandHandler(ILabelRepository labelRepository, IConnectionMultiplexer conn, IMapper mapper)
    {
        _labelRepository = labelRepository;
        _mapper = mapper;
        _redis = conn.GetDatabase();
    }

    public async Task<Result<LabelResponseViewModel>> Handle(UpdateLabelCommand request, CancellationToken cancellationToken)
    {
        var labelFromRepo = await _labelRepository.GetByIdAsync(request.Id);
        if (labelFromRepo == null)
        {
            return Result<LabelResponseViewModel>.Failure("Label not found", ErrorType.NotFound);
        }

        await _redis.KeyDeleteAsync($"labels:{request.Id}");
        
        labelFromRepo.Name = request.Name;

        var res = await _labelRepository.UpdateAsync(labelFromRepo);
        return Result<LabelResponseViewModel>.Success(_mapper.Map<LabelResponseViewModel>(res));
    }
}
