using discussion.Errors;
using discussion.Infrastructure;
using discussion.Kafka;
using discussion.Repositories;
using discussion.Services;
using Confluent.Kafka;
using Microsoft.Extensions.Options;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers(options => options.Filters.Add<GlobalExceptionFilter>());
builder.Services.AddOpenApi();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

builder.Services.Configure<KafkaOptions>(builder.Configuration.GetSection(KafkaOptions.SectionName));

builder.Services.AddSingleton<IProducer<string, string>>(sp =>
{
    var o = sp.GetRequiredService<IOptions<KafkaOptions>>().Value;
    return new ProducerBuilder<string, string>(new ProducerConfig
    {
        BootstrapServers = o.BootstrapServers,
        AllowAutoCreateTopics = true
    }).Build();
});

builder.Services.AddCassandraDiscussion(builder.Configuration);
builder.Services.AddScoped<INoticeRepository, CassandraNoticeRepository>();
builder.Services.AddScoped<INoticeAppService, NoticeAppService>();
builder.Services.AddHostedService<DiscussionInTopicConsumerHostedService>();

var app = builder.Build();

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseAuthorization();
app.MapControllers();
app.Run();

public partial class Program;
