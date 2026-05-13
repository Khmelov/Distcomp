using AutoMapper;
using MediatR;
using Publisher.Application.Repositories;
using Publisher.Application.ViewModel;
using Shared.Commons;
using StackExchange.Redis;

namespace Publisher.Application.Features.Commands.UpdateTopic;

public class UpdateTopicCommandHandler : IRequestHandler<UpdateTopicCommand, Result<TopicResponseViewModel>>
{
    private readonly ITopicRepository _topicRepository;
    private readonly IDatabase _redis;
    private readonly IMapper _mapper;

    public UpdateTopicCommandHandler(ITopicRepository topicRepository, IConnectionMultiplexer conn, IMapper mapper)
    {
        _topicRepository = topicRepository;
        _redis = conn.GetDatabase();
        _mapper = mapper;
    }

    public async Task<Result<TopicResponseViewModel>> Handle(UpdateTopicCommand request, CancellationToken cancellationToken)
    {
        var topicFromRepo = await _topicRepository.GetByIdAsync(request.Id);
        if (topicFromRepo == null)
        {
            return Result<TopicResponseViewModel>.Failure("Topic not found", ErrorType.NotFound);
        }

        await _redis.KeyDeleteAsync($"topics:{request.Id}");

        topicFromRepo.Title = request.Title;
        topicFromRepo.Content = request.Content;
        topicFromRepo.Modified = DateTime.UtcNow; // Обновляем время изменения

        var res = await _topicRepository.UpdateAsync(topicFromRepo);
        return Result<TopicResponseViewModel>.Success(_mapper.Map<TopicResponseViewModel>(res));
    }
}
