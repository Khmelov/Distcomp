using System.Text.Json;
using AutoMapper;
using MediatR;
using Publisher.Application.Repositories;
using Publisher.Application.ViewModel;
using Publisher.Domain.Models;
using Shared.Commons;
using StackExchange.Redis;

namespace Publisher.Application.Features.Commands.CreateTopic;

public class CreateTopicCommandHandler : IRequestHandler<CreateTopicCommand, Result<TopicResponseViewModel>>
{
    private readonly ITopicRepository _topicRepository;
    private readonly IUserRepository _userRepository;
    private readonly IDatabase _redis;
    private readonly IMapper _mapper;

    public CreateTopicCommandHandler(ITopicRepository topicRepository, IConnectionMultiplexer conn, IMapper mapper, IUserRepository userRepository)
    {
        _topicRepository = topicRepository;
        _redis = conn.GetDatabase();
        _mapper = mapper;
        _userRepository = userRepository;
    }

    public async Task<Result<TopicResponseViewModel>> Handle(CreateTopicCommand request, CancellationToken cancellationToken)
    {
        var user = await _userRepository.GetByIdAsync(request.UserId);
        if (user == null)
        {
            return Result<TopicResponseViewModel>.Failure("User not found", ErrorType.NotFound);    
        }
        
        var topicToAdd = new Topic()
        {
            UserId = request.UserId,
            Title = request.Title,
            Content = request.Content,
            Created = DateTime.UtcNow,
            Modified = DateTime.UtcNow
        };

        await _topicRepository.AddAsync(topicToAdd);

        var topicResponse = _mapper.Map<TopicResponseViewModel>(topicToAdd);
        await _redis.StringSetAsync($"topics:{topicToAdd.Id}", JsonSerializer.SerializeToUtf8Bytes(topicResponse));

        return Result<TopicResponseViewModel>.Success(topicResponse);
    }
}
