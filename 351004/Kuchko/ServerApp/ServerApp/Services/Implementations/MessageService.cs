using System.Text.Json;
using Confluent.Kafka;
using ServerApp.Models.Entities;
using ServerApp.Repository;
using ServerApp.Services.Interfaces;
using SharedModels;

namespace ServerApp.Services.Implementations;

public class MessageService(
    KafkaRequestManager rms,
    IConfiguration config,
    IRepository<Article> articleRepo) : IMessageService
{
    private readonly JsonSerializerOptions _options = new() { PropertyNameCaseInsensitive = true };

    private readonly ProducerConfig _pConf = new()
        { BootstrapServers = config["Kafka:BootstrapServers"] ?? "kafka:29092" };

    public MessageResponseTo Create(MessageRequestTo request)
    {
        if (articleRepo.GetById(request.ArticleId) == null) throw new ArgumentException("Article not found");

        var msg = new MessageResponseTo(DateTimeOffset.UtcNow.ToUnixTimeMilliseconds(), request.ArticleId,
            request.Content, MessageState.Pending);
        var ev = new KafkaEvent
            { Action = "CREATE", ArticleId = request.ArticleId, Payload = JsonSerializer.Serialize(msg) };

        using var p = new ProducerBuilder<string, string>(_pConf).Build();
        p.Produce("InTopic",
            new Message<string, string> { Key = ev.ArticleId.ToString(), Value = JsonSerializer.Serialize(ev) });
        return msg; // Сразу возвращаем PENDING
    }

    public MessageResponseTo Update(long id, MessageRequestTo request)
    {
        var msg = new MessageResponseTo(id, request.ArticleId, request.Content, MessageState.Pending);
        var ev = new KafkaEvent
        {
            Action = "UPDATE",
            ArticleId = request.ArticleId,
            Payload = JsonSerializer.Serialize(msg),
            CorrelationId = Guid.NewGuid().ToString()
        };

        var result = SendRequestReply(ev).Result;
        return JsonSerializer.Deserialize<MessageResponseTo>(result.Payload, _options)!;
    }

    public MessageResponseTo GetById(long id)
    {
        // Для упрощения в этом задании ищем по ID (с ALLOW FILTERING в Cassandra)
        var res = RequestReply(new KafkaEvent { Action = "GET", Payload = id.ToString() }).Result;
        return JsonSerializer.Deserialize<MessageResponseTo>(res.Payload)!;
    }

    public IEnumerable<MessageResponseTo> GetAll()
    {
        var res = RequestReply(new KafkaEvent { Action = "GET_ALL" }).Result;
        return JsonSerializer.Deserialize<IEnumerable<MessageResponseTo>>(res.Payload)!;
    }

    public void Delete(long id)
    {
        // Нам нужно знать articleId для удаления в Cassandra. 
        // В реальном API он должен передаваться, тут получим его через GET
        var msg = GetById(id);
        _ = RequestReply(new KafkaEvent { Action = "DELETE", ArticleId = msg.ArticleId, Payload = id.ToString() })
            .Result;
    }

    private async Task<KafkaEvent> RequestReply(KafkaEvent ev)
    {
        var tcs = new TaskCompletionSource<KafkaEvent>();
        rms.Add(ev.CorrelationId, tcs);

        using var p = new ProducerBuilder<string, string>(_pConf).Build();
        await p.ProduceAsync("InTopic",
            new Message<string, string> { Key = ev.ArticleId.ToString(), Value = JsonSerializer.Serialize(ev) });

        if (await Task.WhenAny(tcs.Task, Task.Delay(1000)) == tcs.Task) return await tcs.Task;
        throw new TimeoutException("Discussion service timeout");
    }

    private async Task<KafkaEvent> SendRequestReply(KafkaEvent ev)
    {
        var tcs = new TaskCompletionSource<KafkaEvent>();
        rms.Add(ev.CorrelationId, tcs);

        using var p = new ProducerBuilder<string, string>(_pConf).Build();
        await p.ProduceAsync("InTopic",
            new Message<string, string> { Key = ev.ArticleId.ToString(), Value = JsonSerializer.Serialize(ev) });

        if (await Task.WhenAny(tcs.Task, Task.Delay(1000)) == tcs.Task) return await tcs.Task;
        throw new TimeoutException("Discussion service timeout (1s)");
    }
}