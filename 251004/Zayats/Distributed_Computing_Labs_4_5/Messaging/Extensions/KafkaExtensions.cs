using Confluent.Kafka;
using Messaging.Consumer;
using Messaging.Consumer.Implementations;
using Messaging.Consumer.Interfaces;
using Messaging.KafkaSerialization;
using Messaging.MessageBus.Implementations;
using Messaging.MessageBus.Interfaces;
using Messaging.Producer;
using Messaging.Producer.Implementations;
using Messaging.Producer.Interfaces;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Options;

namespace Messaging.Extensions;

public static class KafkaExtensions
{
    public static IServiceCollection AddKafkaMessageBus(this IServiceCollection services)
    {
        return services.AddSingleton(
            typeof(IMessageBus<,>), 
            typeof(KafkaMessageBus<,>));
    }
    
    public static IServiceCollection AddKafkaConsumer<TK, TV, THandler>(this IServiceCollection services,
        Action<KafkaConsumerConfig> configAction) where THandler : class, IKafkaHandler<TK, TV>
    {
        services.Configure(configAction);
        services.AddScoped<IKafkaHandler<TK, TV>, THandler>();
        services.AddHostedService<BackgroundKafkaConsumer<TK, TV>>();
        
        return services;
    }

    public static IServiceCollection AddKafkaProducer<TK, TV>(this IServiceCollection services,
        Action<KafkaProducerConfig> configAction)
    {
        services.Configure(configAction);
        services.AddSingleton(
            sp =>
            {
                var config = sp.GetRequiredService<IOptions<KafkaProducerConfig>>();

                var builder = new ProducerBuilder<TK, TV>(config.Value)
                    .SetValueSerializer(new Serializer<TV>());

                return builder.Build();
            });

        services.AddSingleton<IKafkaProducer<TK, TV>, KafkaProducer<TK, TV>>();

        return services;
    }
}