using Confluent.Kafka;
using Messaging.Consumer.Interfaces;
using Messaging.KafkaSerialization;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Options;

namespace Messaging.Consumer.Implementations;

public class BackgroundKafkaConsumer<TK, TV> : BackgroundService
{
    private readonly KafkaConsumerConfig _config;
    private readonly IServiceScopeFactory _serviceScopeFactory;

    public BackgroundKafkaConsumer(IOptions<KafkaConsumerConfig> config,
        IServiceScopeFactory serviceScopeFactory)
    {
        _serviceScopeFactory = serviceScopeFactory;
        _config = config.Value;
    }
    
    protected override Task ExecuteAsync(CancellationToken stoppingToken)
    {
        return Task.Run(() =>
        {
            using var consumer = new ConsumerBuilder<TK, TV>(_config)
                .SetValueDeserializer(new Deserializer<TV>())
                .Build();
            consumer.Subscribe(_config.Topic);

            try
            {
                while (!stoppingToken.IsCancellationRequested)
                {
                    var result = consumer.Consume(stoppingToken);
                    if (result != null)
                    {
                        // Для каждого сообщения создаём новый scope
                        using var scope = _serviceScopeFactory.CreateScope();
                        var handler = scope.ServiceProvider.GetRequiredService<IKafkaHandler<TK, TV>>();

                        // Можно синхронно ожидать обработку, так как мы находимся в отдельном потоке
                        handler.HandleAsync(result.Message.Key, result.Message.Value)
                            .GetAwaiter().GetResult();

                        consumer.Commit(result);
                        consumer.StoreOffset(result);
                    }
                }
            }
            catch (OperationCanceledException)
            {
                // Ожидаемое завершение
            }
            catch (Exception ex)
            {
                // Здесь можно добавить логирование ошибки
                Console.WriteLine($"Ошибка в цикле потребления: {ex.Message}");
            }
            finally
            {
                consumer.Close();
            }
        }, stoppingToken);
    }

}