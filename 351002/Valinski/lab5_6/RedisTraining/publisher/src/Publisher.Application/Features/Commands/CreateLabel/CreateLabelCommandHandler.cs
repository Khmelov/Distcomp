using System.Text.Json;
using AutoMapper;
using MediatR;
using Publisher.Application.Repositories;
using Publisher.Application.ViewModel;
using Publisher.Domain.Models;
using Shared.Commons;
using StackExchange.Redis;

namespace Publisher.Application.Features.Commands.CreateLabel;

public class CreateLabelCommandHandler : IRequestHandler<CreateLabelCommand, Result<LabelResponseViewModel>>
{
    private readonly ILabelRepository _labelRepository;
    private readonly IDatabase _redis;
    private readonly IMapper _mapper;
        
    public CreateLabelCommandHandler(ILabelRepository labelRepository, IConnectionMultiplexer conn, IMapper mapper)
    {
        _labelRepository = labelRepository;
        _redis = conn.GetDatabase();
        _mapper = mapper;
    }
    
    public async Task<Result<LabelResponseViewModel>> Handle(CreateLabelCommand request, CancellationToken cancellationToken)
    {
        var labelToAdd = new Label()
        {
            Name = request.Name
        };

        await _labelRepository.AddAsync(labelToAdd);

        var labelResponse = _mapper.Map<LabelResponseViewModel>(labelToAdd);
        await _redis.StringSetAsync($"labels:{labelToAdd.Id}", JsonSerializer.SerializeToUtf8Bytes(labelResponse));
        
        return Result<LabelResponseViewModel>.Success(labelResponse);
    }
}
