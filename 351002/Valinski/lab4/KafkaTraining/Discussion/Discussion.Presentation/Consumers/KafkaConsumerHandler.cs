using System.Text;
using System.Text.Json;
using Confluent.Kafka;
using Discussion.Domain.Abstractions;
using Discussion.Domain.Models;
using Discussion.Presentation.Contracts;
using Microsoft.Extensions.Options;

namespace Discussion.Presentation.Consumers;

public class KafkaConsumerHandler : BackgroundService
{
    private readonly IServiceScopeFactory _serviceScopeFactory;
    private readonly IProducer<string, string> _producer;
    private readonly KafkaConnectionOptions _options;
    private readonly IConsumer<string, string> _consumer;

    public KafkaConsumerHandler(IServiceScopeFactory serviceScopeFactory, IProducer<string, string> producer,
        IOptions<KafkaConnectionOptions> options)
    {
        _serviceScopeFactory = serviceScopeFactory;
        _producer = producer;
        _options = options.Value;

        var consConfig = new ConsumerConfig()
        {
            BootstrapServers = _options.BootstrapServers,
            GroupId = _options.GroupId,
            AutoOffsetReset = AutoOffsetReset.Earliest,
        };

        _consumer = new ConsumerBuilder<string, string>(consConfig).Build();
    }

    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        Task.Run(async () =>
        {
            _consumer.Subscribe(_options.InTopic);

            try
            {
                while (!stoppingToken.IsCancellationRequested)
                {
                    var msg = _consumer.Consume(stoppingToken);
                    var headers = msg.Message.Headers;
                    var corrIdB = headers.FirstOrDefault(x => x.Key == "CorrelationId")?.GetValueBytes();
                    var actionB = headers.FirstOrDefault(x => x.Key == "Action")?.GetValueBytes();
                    var action = actionB is null ? null : Encoding.UTF8.GetString(actionB);

                    var replyToB = headers.FirstOrDefault(x => x.Key == "ReplyTo")?.GetValueBytes();
                    var replyTo = replyToB is null ? null : Encoding.UTF8.GetString(replyToB);

                    var value = msg.Message.Value;
                    var scope = _serviceScopeFactory.CreateScope();
                    var repo = scope.ServiceProvider.GetRequiredService<IReactionRepository>();

                    //get put delete  
                    switch (action)
                    {
                        case "GetAll":
                            var messageToSend = new Message<string, string>()
                            {
                                Headers = new Headers()
                                {
                                    { "CorrelationId", corrIdB },
                                },
                                Value = JsonSerializer.Serialize(await repo.GetAllReactions()),
                            };
                            await _producer.ProduceAsync(replyTo, messageToSend);
                            break;
                        case "Create":
                            var reactionDto = JsonSerializer.Deserialize<CreateReactionRequest>(value)!;
                            var reaction = new Reaction()
                            {
                                Id = reactionDto.Id,
                                Content = reactionDto.Content,
                                Country = reactionDto.Country,
                                TopicId = reactionDto.TopicId,
                            };

                            await repo.CreateReaction(reaction);
                            break;
                        case "GetById":
                            var reactionFromRepos = await repo.GetById(Convert.ToInt64(value));
                            var msgById = new Message<string, string>()
                            {
                                Headers = new Headers()
                                {
                                    { "CorrelationId", corrIdB },
                                },
                                Value = JsonSerializer.Serialize(reactionFromRepos),
                            };
                            await _producer.ProduceAsync(replyTo, msgById);
                            break;
                        case "DeleteById":
                            long deletionId = Convert.ToInt64(value);
                            var reactionFromRepo = await repo.GetById(deletionId);
                            var deletionMsg = new Message<string, string>()
                            {
                                Headers = new Headers()
                                {
                                    { "CorrelationId", corrIdB },
                                },
                            };

                            if (reactionFromRepo is not null)
                            {
                                deletionMsg.Value = "Success";
                                await repo.DeleteReaction(deletionId);
                            }
                            else
                            {
                                deletionMsg.Value = "Error";
                            }


                            await _producer.ProduceAsync(replyTo, deletionMsg);
                            break;
                        case "Update":
                            var deserializedReaction = JsonSerializer.Deserialize<Reaction>(value);
                            var updatedReaction = await repo.UpdateReaction(deserializedReaction);
                            var serialize = JsonSerializer.Serialize(updatedReaction);
                            var updatedReactionMsg = new Message<string, string>()
                            {
                                Headers = new Headers()
                                {
                                    { "CorrelationId", corrIdB },
                                },
                                Value = serialize,
                            };
                            await _producer.ProduceAsync(replyTo, updatedReactionMsg);
                            break;
                        default:
                            Console.WriteLine($"loh");
                            break;
                    }
                }
            }
            catch (Exception e)
            {
                Console.WriteLine(e);
                throw;
            }
            finally
            {
                _consumer.Close();
                _consumer.Dispose();
            }
        });
    }
}
