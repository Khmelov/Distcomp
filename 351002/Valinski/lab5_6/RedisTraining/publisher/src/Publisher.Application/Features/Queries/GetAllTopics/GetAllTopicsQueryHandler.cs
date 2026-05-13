using AutoMapper;
using MediatR;
using Publisher.Application.Repositories;
using Publisher.Application.ViewModel;
using Shared.Commons;
using StackExchange.Redis;

namespace Publisher.Application.Features.Queries.GetAllTopics;

public class GetAllTopicsQueryHandler : IRequestHandler<GetAllTopicsQuery, Result<List<TopicResponseViewModel>>>
{
    private readonly IMapper _mapper;
    private readonly ITopicRepository _topicRepository;
    private readonly IDatabase _database;

    public GetAllTopicsQueryHandler(IConnectionMultiplexer conn, ITopicRepository topicRepository, IMapper mapper)
    {
        _topicRepository = topicRepository;
        _mapper = mapper;
        _database = conn.GetDatabase();
    }

    public async Task<Result<List<TopicResponseViewModel>>> Handle(GetAllTopicsQuery request, CancellationToken cancellationToken)
    {
        // todo: make data retrieving from cache

        var tempResult = await _topicRepository.GetAllAsync();
        
        return Result<List<TopicResponseViewModel>>.Success(_mapper.Map<List<TopicResponseViewModel>>(tempResult));
    }
}
