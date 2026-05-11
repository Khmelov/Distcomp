using AutoMapper;
using Confluent.Kafka;
using EFCore.NamingConventions;
using lab1.Data;
using lab1.Errors;
using lab1.Infrastructure;
using lab1.Kafka;
using lab1.Mapping;
using lab1.Models.Entities;
using lab1.Repositories.Ef;
using lab1.Repositories.Interfaces;
using lab1.Services.Implementations;
using lab1.Services.Interfaces;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Options;
using StackExchange.Redis;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers(options => options.Filters.Add<GlobalExceptionFilter>());
builder.Services.AddOpenApi();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

builder.Services.Configure<KafkaTransportOptions>(builder.Configuration.GetSection(KafkaTransportOptions.SectionName));
builder.Services.Configure<RedisConnectionOptions>(builder.Configuration.GetSection(RedisConnectionOptions.SectionName));
builder.Services.AddSingleton<IConnectionMultiplexer>(sp =>
    ConnectionMultiplexer.Connect(sp.GetRequiredService<IOptions<RedisConnectionOptions>>().Value.Configuration));
builder.Services.AddSingleton<IRedisJsonCache, RedisJsonCache>();

builder.Services.AddSingleton<IProducer<string, string>>(sp =>
{
    var o = sp.GetRequiredService<IOptions<KafkaTransportOptions>>().Value;
    return new ProducerBuilder<string, string>(new ProducerConfig
    {
        BootstrapServers = o.BootstrapServers,
        AllowAutoCreateTopics = true
    }).Build();
});

builder.Services.AddSingleton<NoticeRpcCorrelationRegistry>();
builder.Services.AddHostedService<PublisherOutTopicConsumerHostedService>();

builder.Services.AddDbContext<AppDbContext>(options =>
    options.UseNpgsql(builder.Configuration.GetConnectionString("DefaultConnection"))
        .UseSnakeCaseNamingConvention());

builder.Services.AddAutoMapper(cfg =>
{
    cfg.AddProfile<EditorProfile>();
    cfg.AddProfile<IssueProfile>();
    cfg.AddProfile<LabelProfile>();
});

builder.Services.AddHttpClient("Discussion", (sp, client) =>
{
    var url = builder.Configuration["Discussion:BaseUrl"] ?? "http://localhost:24130/";
    if (!url.EndsWith('/'))
        url += "/";
    client.BaseAddress = new Uri(url);
    client.Timeout = TimeSpan.FromSeconds(30);
});

builder.Services.AddScoped<IEntityRepository<Editor>, EfEntityRepository<Editor>>();
builder.Services.AddScoped<IEntityRepository<Label>, EfEntityRepository<Label>>();
builder.Services.AddScoped<IssueEfRepository>();
builder.Services.AddScoped<IIssueRepository>(sp => sp.GetRequiredService<IssueEfRepository>());
builder.Services.AddScoped<IEntityRepository<Issue>>(sp => sp.GetRequiredService<IssueEfRepository>());

builder.Services.AddScoped<EditorService>();
builder.Services.AddScoped<IEditorService, CachingEditorService>();

builder.Services.AddScoped<IssueService, IssueService>();
builder.Services.AddScoped<IIssueService, CachingIssueService>();

builder.Services.AddScoped<NoticeKafkaService>();
builder.Services.AddScoped<INoticeService, CachingNoticeService>();

builder.Services.AddScoped<IDiscussionNoticeCleanup, DiscussionNoticeCleanup>();

builder.Services.AddScoped<LabelService>();
builder.Services.AddScoped<ILabelService, CachingLabelService>();

var app = builder.Build();

using (var scope = app.Services.CreateScope())
{
    var db = scope.ServiceProvider.GetRequiredService<AppDbContext>();
    var notices = scope.ServiceProvider.GetRequiredService<INoticeService>();
    await DbSeeder.SeedAsync(db, notices);
}

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseAuthorization();
app.MapControllers();
app.Run();

public partial class Program;
