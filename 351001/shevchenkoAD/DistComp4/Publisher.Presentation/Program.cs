using System.Text.Json.Serialization;
using Confluent.Kafka;
using Publisher.Domain.Entities;
using Publisher.Domain.Interfaces;
using Publisher.Infrastructure;
using Publisher.Infrastructure.Repositories;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.OpenApi.Models;
using Publisher.Application.Services;
using Publisher.Application.Services.Interfaces;
using Publisher.Application.Services.Profiles;
using Publisher.Presentation.Background;
using Publisher.Presentation.Middleware;
using Shared.DTOs.Responses;

namespace Publisher.Presentation;

public class Program {
    public static void Main(string[] args) {
        var builder = WebApplication.CreateBuilder(args);

        builder.WebHost.UseUrls("http://localhost:24110");

        builder.Services.AddControllers()
            .AddJsonOptions(options =>
            {
                
                options.JsonSerializerOptions.Converters.Add(new JsonStringEnumConverter());
            });
        
        var bootstrapServers = "localhost:9092";


        var producerConfig = new ProducerConfig 
        { 
            BootstrapServers = bootstrapServers,
            Acks = Acks.All 
        };


        var consumerConfig = new ConsumerConfig
        {
            BootstrapServers = bootstrapServers,
            GroupId = "publisher-response-group", 
            AutoOffsetReset = AutoOffsetReset.Earliest,
            EnableAutoCommit = true
        };
        
        builder.Services.AddControllers()
            .ConfigureApiBehaviorOptions(options =>
            {
                options.InvalidModelStateResponseFactory = context =>
                {
                    var errorMessage = "Invalid data format in request body";
                    var errorResponse = new ErrorResponse(errorMessage, 40000); 
                    return new BadRequestObjectResult(errorResponse);
                };
            });

        builder.Services.AddEndpointsApiExplorer();

        builder.Services.AddSwaggerGen(options => {
            options.SwaggerDoc("v1", new OpenApiInfo {
                Title = "Task330 NoSQL",
                Version = "v1.0"
            });
        });
        
        var connectionString = builder.Configuration.GetConnectionString("PostgresDb");
        builder.Services.AddDbContext<AppDbContext>(options =>
                                                        options.UseNpgsql(connectionString));
        
        builder.Services.AddSingleton<IProducer<string, string>>(new ProducerBuilder<string, string>(
            new ProducerConfig { BootstrapServers = bootstrapServers }).Build());

        builder.Services.AddSingleton<IConsumer<string, string>>(new ConsumerBuilder<string, string>(
            new ConsumerConfig { 
                BootstrapServers = bootstrapServers, 
                GroupId = "publisher-group", 
                AutoOffsetReset = AutoOffsetReset.Earliest 
            }).Build());
        
        builder.Services.AddScoped<IRepository<Author>, DbAuthorRepository>();
        builder.Services.AddScoped<IIssueRepository,  DbIssueRepository>();
        builder.Services.AddScoped<IRepository<Issue>,  DbIssueRepository>();
        builder.Services.AddScoped<IRepository<Label>, DbLabelRepository>();
        
        builder.Services.AddScoped<ICommentService, KafkaCommentService>();
        builder.Services.AddScoped<IAuthorService, AuthorService>();
        builder.Services.AddScoped<IIssueService, IssueService>();
        builder.Services.AddScoped<ILabelService, LabelService>();
        
        builder.Services.AddHostedService<KafkaResponseListener>();

        builder.Services.AddAutoMapper(cfg => { cfg.AddProfile<MappingProfile>(); });

        var app = builder.Build();
        
        using (var scope = app.Services.CreateScope())
        {
            var db = scope.ServiceProvider.GetRequiredService<AppDbContext>();
            db.Database.Migrate();
        }

        app.UseSwagger();
        app.UseSwaggerUI(options => { options.SwaggerEndpoint("/swagger/v1/swagger.json", "Task330 NoSQL"); });

        app.UseMiddleware<ExceptionHandlingMiddleware>();
        app.MapControllers();

        app.Run();
    }
}