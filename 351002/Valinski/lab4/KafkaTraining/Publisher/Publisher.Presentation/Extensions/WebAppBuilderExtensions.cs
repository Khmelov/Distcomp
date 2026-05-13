using System.Reflection;
using Confluent.Kafka;
using FluentValidation;
using Infrastructure;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Options;
using Publisher.Presentation.Clients;
using Publisher.Presentation.Options;
using Publisher.Presentation.Profiles;

namespace Publisher.Presentation.Extensions;

public static class WebAppBuilderExtensions
{
    public static WebApplicationBuilder AddDependencies(this WebApplicationBuilder builder)
    {
        builder.Services.AddDbContext<PublisherDbContext>(options =>
        {
            var connectionString = builder.Configuration.GetValue<string>("PostgresConnection");
            options.UseNpgsql(connectionString);
        });

        builder.Services.AddOpenApi();
        builder.Services.AddSwaggerGen();

        builder.Services.Configure<KafkaReactionConnectionOptions>(
            builder.Configuration.GetSection(nameof(KafkaReactionConnectionOptions)));
        
        builder.Services.AddSingleton<IConsumer<string, string>>(x =>
        {
            var opt =  x.GetRequiredService<IOptions<KafkaReactionConnectionOptions>>();
            var consumerConfig = new ConsumerConfig()
            {
                BootstrapServers = opt.Value.BootstrapServers,
                GroupId = opt.Value.GroupId,
                AutoOffsetReset = AutoOffsetReset.Earliest
            };
            return new ConsumerBuilder<string, string>(consumerConfig).Build();
        });
        
        builder.Services.AddHostedService<ReactionConsumer>();
        
        builder.Services.AddAutoMapper(cfg =>
        {
            cfg.AddProfile(typeof(UserProfile));
            cfg.AddProfile(typeof(TopicProfile));
            cfg.AddProfile(typeof(LabelProfile));
        });
        builder.Services.AddControllers();

        builder.Services.AddSingleton<ReactionsServiceClient>();

        builder.Services.AddValidatorsFromAssembly(Assembly.GetExecutingAssembly());
        
        return builder;
    }
}
