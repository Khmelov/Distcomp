using Confluent.Kafka;
using FluentValidation;
using IdGen;
using MassTransit;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using Microsoft.OpenApi;
using Publisher.Application;
using Publisher.Application.Features.Queries.GetUserById;
using Publisher.Application.Profiles;
using Publisher.Application.Repositories;
using Publisher.Infrastructure;
using Publisher.Infrastructure.Broker.Consumers;
using Publisher.Infrastructure.DbContexts;
using Publisher.Infrastructure.Repositories;
using Publisher.Presentation.Profiles;
using Publisher.Presentation.Services;
using Publisher.Presentation.Validators;
using Shared.Commons;
using Shared.Messages;
using StackExchange.Redis;

namespace Publisher.Presentation.DiConfiguration;

public static class WebAppBuilderExtensions
{
    public static WebApplicationBuilder AddDependencies(this WebApplicationBuilder builder)
    {
        // controllers        
        builder.Services.AddOpenApi();

        // Db
        builder.Services.AddDbContext<PublisherDbContext>(options =>
        {
            options.UseNpgsql(builder.Configuration.GetConnectionString("DbConnectionString"));
        });

        // error handler
        builder.Services.AddExceptionHandler<GlobalExceptionHandler>();
        builder.Services.AddProblemDetails();

        // redis    
        builder.Services.AddSingleton<IConnectionMultiplexer>(_ =>
            ConnectionMultiplexer.Connect(builder.Configuration.GetConnectionString("RedisConnectionString")!));

        // automapper
        builder.Services.AddAutoMapper(cfg => { },
            typeof(UserApplicationProfile).Assembly,
            typeof(UserProfile).Assembly);

        // swagger
        builder.Services.AddSwaggerGen(options =>
        {
            options.SwaggerDoc("v1", new OpenApiInfo
            {
                Version = "v1",
                Title = "Publisher API",
                Description = "API для модуля публикаций и обсуждений",
                Contact = new OpenApiContact
                {
                    Name = "Артём Валинский",
                    Email = "valinskiyartem@gmail.com"
                }
            });
        });

        // mediatR
        builder.Services.AddMediatR(cfg =>
            cfg.RegisterServicesFromAssembly(typeof(GetUserByIdQuery).Assembly)
        );

        // repositories
        builder.Services.AddScoped<IUserRepository, UserRepository>();
        builder.Services.AddScoped<ILabelRepository, LabelRepository>();
        builder.Services.AddScoped<ITopicRepository, TopicRepository>();

        //validators
        builder.Services.AddValidatorsFromAssemblies([
            typeof(UserRequestValidator).Assembly, typeof(TopicRequestValidator).Assembly
        ]);

        // kafka masstransit
        builder.Services.AddMassTransit(x =>
        {
            x.UsingInMemory();
            x.AddRider(rider =>
            {
                // sends requests to Discussion service
                rider.AddProducer<ReactionMessage>("InTopic");

                rider.AddConsumer<ReactionConsumer>();

                rider.UsingKafka((context, k) =>
                {
                    k.Host("localhost:9092");

                    k.TopicEndpoint<ReactionMessage>("OutTopic", "publisher-group", e =>
                    {
                        e.AutoOffsetReset = AutoOffsetReset.Latest;
                        e.ConfigureConsumer<ReactionConsumer>(context);
                    });
                });
            });
        });
        builder.Services.AddSingleton<BrokerHelper>(new BrokerHelper());

        // idgen
        builder.Services.AddScoped<IdGenerator>(_ => new IdGenerator(DateTime.Now.Second));

        // auth
        builder.Services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
            .AddJwtBearer(options =>
            {
                options.TokenValidationParameters = new TokenValidationParameters
                {
                    ValidateIssuer = false,
                    ValidateAudience = false,
                    ValidateLifetime = true,
                    ValidateIssuerSigningKey = true,
                    
                    IssuerSigningKey = new SymmetricSecurityKey(
                        "SuperMegaSecretKeyForJwtTokenValidation"u8.ToArray()),
                    ClockSkew = TimeSpan.Zero,
                    
                };
            });
        builder.Services.AddAuthorization();
        builder.Services.AddScoped<IJwtGenerator, JwtGenerator>();

        return builder;
    }
}
