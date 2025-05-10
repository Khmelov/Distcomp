using Confluent.Kafka;
using Discussion.DTO;
using Discussion.Models;
using Discussion.Services;
using System;
using System.Linq;
using System.Text.Json;
using System.Threading;
using System.Threading.Tasks;

namespace Discussion
{
    public class KafkaReactionConsumerService : IDisposable
    {
        private readonly ConsumerConfig _consumerConfig;
        private readonly ProducerConfig _producerConfig;
        private readonly IReactionService _reactionService;
        private readonly IProducer<string, string> _producer;

        public KafkaReactionConsumerService(IReactionService reactionService)
        {
            _reactionService = reactionService;
            _consumerConfig = new ConsumerConfig
            {
                BootstrapServers = "localhost:9092",
                GroupId = "discussion-group",
                AutoOffsetReset = AutoOffsetReset.Earliest,
                SecurityProtocol = SecurityProtocol.Plaintext
            };
            _producerConfig = new ProducerConfig
            {
                BootstrapServers = "localhost:9092",
                SecurityProtocol = SecurityProtocol.Plaintext
            };

            _producer = new ProducerBuilder<string, string>(_producerConfig).Build();
        }

        public async Task RunAsync(CancellationToken stoppingToken)
        {
            using var consumer = new ConsumerBuilder<string, string>(_consumerConfig).Build();

            try
            {
                consumer.Subscribe("InTopic");
                Console.WriteLine("Consumer успешно подписался на InTopic.");
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Ошибка подписки на InTopic: {ex.Message}");
                return;
            }

            while (!stoppingToken.IsCancellationRequested)
            {
                try
                {
                    var consumeResult = consumer.Consume(TimeSpan.FromSeconds(1));
                    if (consumeResult == null)
                        continue;

                    var reactionDto = JsonSerializer.Deserialize<ReactionResponseTo>(consumeResult.Message.Value);
                    if (reactionDto == null)
                        continue;

                    reactionDto.State = ModerationAlgorithm(reactionDto.Content);

                    var existingReaction = await _reactionService.GetReactionByIdAsync(reactionDto.Id);
                    if (existingReaction == null)
                    {
                        var newReaction = new Reaction
                        {
                            Id = reactionDto.Id,
                            TopicId = reactionDto.TopicId,
                            Content = reactionDto.Content,
                            Modified = DateTime.UtcNow
                        };
                        await _reactionService.CreateReactionAsync(newReaction);
                    }
                    else
                    {
                        existingReaction.Content = reactionDto.Content;
                        existingReaction.Modified = DateTime.UtcNow;
                        await _reactionService.UpdateReactionAsync(reactionDto.Id, existingReaction);
                    }

                    var messageValue = JsonSerializer.Serialize(reactionDto);
                    await _producer.ProduceAsync("OutTopic", new Message<string, string>
                    {
                        Key = reactionDto.TopicId.ToString(),
                        Value = messageValue
                    }, stoppingToken);

                    Console.WriteLine($"Обработано сообщение для заметки {reactionDto.Id}");
                }
                catch (ConsumeException ex)
                {
                    Console.WriteLine($"Ошибка потребления сообщения: {ex.Error.Reason}");
                    await Task.Delay(2000, stoppingToken);
                }
                catch (ProduceException<string, string> ex)
                {
                    Console.WriteLine($"Ошибка отправки сообщения: {ex.Error.Reason}");
                    await Task.Delay(2000, stoppingToken);
                }
                catch (Exception ex)
                {
                    Console.WriteLine($"Непредвиденная ошибка во внутреннем цикле: {ex.Message}");
                    await Task.Delay(2000, stoppingToken);
                }
            }

            consumer.Close();
        }

        private ReactionState ModerationAlgorithm(string content)
        {
            string[] stopWords = { "badword1", "badword2" };
            return stopWords.Any(word => content.Contains(word, StringComparison.OrdinalIgnoreCase))
                ? ReactionState.DECLINE
                : ReactionState.APPROVE;
        }

        public void Dispose()
        {
            _producer?.Dispose();
        }
    }
}
