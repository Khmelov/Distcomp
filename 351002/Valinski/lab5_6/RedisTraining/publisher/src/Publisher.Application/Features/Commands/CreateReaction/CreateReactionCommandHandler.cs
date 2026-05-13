using System.Text.Json;
using IdGen;
using MassTransit;
using MediatR;
using Publisher.Application.Repositories;
using Publisher.Application.ViewModel;
using Shared.Commons;
using Shared.Messages;

namespace Publisher.Application.Features.Commands.CreateReaction;

public class CreateReactionCommandHandler : IRequestHandler<CreateReactionCommand, Result<ReactionResponseViewModel>>
{
    private readonly ITopicProducer<ReactionMessage> _producer;
    private readonly ITopicRepository _repository;
    private readonly IdGenerator _idGenerator;

    public CreateReactionCommandHandler(IdGenerator idGenerator, ITopicRepository repository, ITopicProducer<ReactionMessage> producer)
    {
        _idGenerator = idGenerator;
        _repository = repository;
        _producer = producer;
    }

    public async Task<Result<ReactionResponseViewModel>> Handle(CreateReactionCommand request, CancellationToken cancellationToken)
    {
        var topic = await _repository.GetByIdAsync(request.TopicId);

        if (topic == null)
        {
            return Result<ReactionResponseViewModel>.Failure("Topic not found", ErrorType.NotFound);
        }

        var id = _idGenerator.CreateId();
        var requestBrokerMsg = new CreateReactionRequestMsg()
        {
            Id = id,
            TopicId = request.TopicId,
            Content = request.Content,
            Country = request.Country,
        };

        var message = new ReactionMessage()
        {
            ActionType = ReactionMessages.Create,
            Entity = JsonSerializer.SerializeToElement(requestBrokerMsg),
        };
        
        await _producer.Produce(message);
        var reactionViewModel = new ReactionResponseViewModel()
        {
            Id = id,
            TopicId = request.TopicId,
            Content = request.Content,
            Country = request.Country,
        };
        
        return Result<ReactionResponseViewModel>.Success(reactionViewModel);
    }
}
