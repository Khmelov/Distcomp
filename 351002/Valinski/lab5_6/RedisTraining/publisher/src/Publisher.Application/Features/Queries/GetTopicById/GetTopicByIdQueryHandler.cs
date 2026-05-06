using System.Text.Json;
using AutoMapper;
using MediatR;
using Publisher.Application.Repositories;
using Publisher.Application.ViewModel;
using Shared.Commons;
using StackExchange.Redis;

namespace Publisher.Application.Features.Queries.GetTopicById;

public class GetTopicByIdQueryHandler : IRequestHandler<GetTopicByIdQuery, Result<TopicResponseViewModel>>
{
    private readonly ITopicRepository _topicRepository;
    private readonly IDatabase _redis;
    private readonly IMapper _mapper;

    public GetTopicByIdQueryHandler(ITopicRepository topicRepository, IConnectionMultiplexer conn, IMapper mapper)
    {
        _topicRepository = topicRepository;
        _redis = conn.GetDatabase();
        _mapper = mapper;
    }

    public async Task<Result<TopicResponseViewModel>> Handle(GetTopicByIdQuery request, CancellationToken cancellationToken)
    {
        var cachedTopic = await _redis.StringGetAsync($"topics:{request.Id}");
        if (cachedTopic.HasValue && !cachedTopic.IsNull)
        {
            var cachedResult = JsonSerializer.Deserialize<TopicResponseViewModel>(cachedTopic.ToString())!;
            return Result<TopicResponseViewModel>.Success(cachedResult);
        }

        var topic = await _topicRepository.GetByIdAsync(request.Id);
        if (topic == null)
        {
            return Result<TopicResponseViewModel>.Failure("Topic not found", ErrorType.NotFound);
        }

        var responseViewModel = _mapper.Map<TopicResponseViewModel>(topic);
        await _redis.StringSetAsync($"topics:{request.Id}", JsonSerializer.SerializeToUtf8Bytes(responseViewModel));

        return Result<TopicResponseViewModel>.Success(responseViewModel);
    }
}
