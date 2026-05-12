using System.Collections.Concurrent;
using System.Text.Json;
using Confluent.Kafka;
using Discussion.src.NewsPortal.Discussion.Application.Dtos.ResponseTo;
using Publisher.src.NewsPortal.Publisher.Application.Dtos;
using Publisher.src.NewsPortal.Publisher.Domain.Entities;

namespace Publisher.src.NewsPortal.Publisher.Infrastructure.Messaging
{
    public class KafkaConsumerService : BackgroundService
    {
        private readonly ILogger<KafkaConsumerService> _logger;
        private readonly IConsumer<string, string> _consumer;
        private readonly IKafkaProducerService _kafkaProducer;
        private readonly string _topic;

        private readonly ConcurrentDictionary<long, CacheEntry<NoteResponseTo>> _noteCache = new();
        private readonly LinkedList<long> _accessOrder = new();
        private readonly ReaderWriterLockSlim _cacheLock = new();

        private readonly int _maxCacheSize;
        private readonly TimeSpan _cacheEntryTtl;
        private readonly TimeSpan _cleanupInterval;

        // TaskCompletionSource для ожидания ответов на GET запросы
        private readonly ConcurrentDictionary<long, TaskCompletionSource<NoteResponseTo>> _pendingGetRequests = new();

        public KafkaConsumerService(IConfiguration configuration, ILogger<KafkaConsumerService> logger, IKafkaProducerService kafkaProducer)
        {
            _logger = logger;
            _kafkaProducer = kafkaProducer;
            _topic = configuration["Kafka:OutTopic"] ?? "OutTopic";

            _maxCacheSize = configuration.GetValue<int>("Cache:MaxSize", 1000);
            _cacheEntryTtl = TimeSpan.FromSeconds(configuration.GetValue<int>("Cache:EntryTtlSeconds", 300));
            _cleanupInterval = TimeSpan.FromSeconds(configuration.GetValue<int>("Cache:CleanupIntervalSeconds", 60));

            var config = new ConsumerConfig
            {
                BootstrapServers = configuration["Kafka:BootstrapServers"] ?? "localhost:9092",
                GroupId = "publisher-consumer-group",
                AutoOffsetReset = AutoOffsetReset.Earliest,
                EnableAutoCommit = false
            };

            _consumer = new ConsumerBuilder<string, string>(config).Build();
        }

        protected override async Task ExecuteAsync(CancellationToken stoppingToken)
        {
            _consumer.Subscribe(_topic);
            _logger.LogInformation("Publisher subscribed to {OutTopic}, cache max size: {MaxSize}", _topic, _maxCacheSize);

            _ = Task.Run(() => CleanupExpiredEntries(stoppingToken), stoppingToken);

            try
            {
                while (!stoppingToken.IsCancellationRequested)
                {
                    try
                    {
                        var consumeResult = _consumer.Consume(TimeSpan.FromMilliseconds(100));

                        if (consumeResult != null)
                        {
                            var message = JsonSerializer.Deserialize<KafkaNoteMessage>(consumeResult.Message.Value);

                            if (message?.Data != null)
                            {
                                await ProcessReceivedMessage(message);
                            }

                            _consumer.Commit(consumeResult);
                        }
                    }
                    catch (ConsumeException ex)
                    {
                        _logger.LogError(ex, "Error consuming message from Kafka");
                    }
                    catch (Exception ex)
                    {
                        _logger.LogError(ex, "Error processing message");
                    }

                    await Task.Delay(10, stoppingToken);
                }
            }
            finally
            {
                _consumer.Close();
            }
        }

        private async Task ProcessReceivedMessage(KafkaNoteMessage message)
        {
            var action = message.Action.ToUpper();
            var note = message.Data;

            switch (action)
            {
                case "CREATED":
                case "UPDATED":
                    var response = new NoteResponseTo
                    {
                        Id = note.Id,
                        NewsId = note.NewsId,
                        Content = note.Content,
                        State = note.State
                    };
                    AddOrUpdateInternal(response);
                    _logger.LogDebug("Cached note {Id} with state {State}", response.Id, response.State);
                    break;

                case "DELETED":
                    RemoveNoteInternal(note.Id);
                    _logger.LogDebug("Removed note {Id} from cache", note.Id);
                    break;

                case "FOUND":
                    var foundNote = new NoteResponseTo
                    {
                        Id = note.Id,
                        NewsId = note.NewsId,
                        Content = note.Content,
                        State = note.State
                    };
                    AddOrUpdateInternal(foundNote);

                    // Завершаем ожидающий GET запрос
                    if (_pendingGetRequests.TryRemove(note.Id, out var tcs))
                    {
                        tcs.TrySetResult(foundNote);
                    }
                    _logger.LogDebug("Note {Id} found and delivered", note.Id);
                    break;

                case "ALL_NOTES":
                    var notes = JsonSerializer.Deserialize<List<NoteResponseTo>>(note.Content);
                    if (notes != null)
                    {
                        foreach (var n in notes)
                        {
                            AddOrUpdateInternal(n);
                        }
                        _logger.LogDebug("Cached {Count} notes from ALL_NOTES response", notes.Count);
                    }
                    break;

                case "NOT_FOUND":
                    if (_pendingGetRequests.TryRemove(note.Id, out var notFoundTcs))
                    {
                        notFoundTcs.TrySetResult(null);
                    }
                    _logger.LogDebug("Note {Id} not found", note.Id);
                    break;

                case "ERROR":
                    _logger.LogError("Error from Discussion: {Content}", note.Content);
                    if (_pendingGetRequests.TryRemove(note.Id, out var errorTcs))
                    {
                        errorTcs.TrySetException(new Exception(note.Content));
                    }
                    break;

                default:
                    _logger.LogWarning("Unknown action: {Action}", action);
                    break;
            }
        }

        // Асинхронный GET запрос через Kafka
        public async Task<NoteResponseTo?> GetNoteByIdAsync(long id, CancellationToken cancellationToken = default)
        {
            // Сначала проверяем кеш
            var cached = GetNoteByIdInternal(id);
            if (cached != null)
            {
                return cached;
            }

            // Создаем TaskCompletionSource для ожидания ответа
            var tcs = new TaskCompletionSource<NoteResponseTo>();
            _pendingGetRequests.TryAdd(id, tcs);

            // Отправляем запрос в Kafka
            var requestMessage = new KafkaNoteMessage
            {
                Action = "GET",
                Data = new Note { Id = id }
            };
            await _kafkaProducer.SendAsync(_topic, requestMessage);

            // Ждем ответ с таймаутом
            using var cts = CancellationTokenSource.CreateLinkedTokenSource(cancellationToken);
            cts.CancelAfter(TimeSpan.FromSeconds(5));

            try
            {
                using (cts.Token.Register(() => tcs.TrySetCanceled()))
                {
                    return await tcs.Task.WaitAsync(cts.Token);
                }
            }
            catch (OperationCanceledException)
            {
                _pendingGetRequests.TryRemove(id, out _);
                _logger.LogWarning("GET request for note {Id} timed out", id);
                return null;
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error getting note {Id} via Kafka", id);
                return null;
            }
        }

        // Асинхронный GET_ALL запрос через Kafka
        public async Task<IEnumerable<NoteResponseTo>> GetAllNotesAsync(CancellationToken cancellationToken = default)
        {
            // Сначала проверяем кеш
            var cached = GetAllNotesInternal();
            if (cached.Any())
            {
                return cached;
            }

            // Создаем TaskCompletionSource для ожидания ответа
            var tcs = new TaskCompletionSource<List<NoteResponseTo>>();
            var requestId = Guid.NewGuid().ToString();

            // Отправляем запрос в Kafka
            var requestMessage = new KafkaNoteMessage
            {
                Action = "GET_ALL",
                Data = new Note { Id = 0 }
            };
            await _kafkaProducer.SendAsync(_topic, requestMessage);

            // Регистрируем временный обработчик для ответа
            var registration = new PendingAllNotesRequest
            {
                Tcs = tcs,
                CancellationToken = cancellationToken
            };

            _pendingAllNotesRequest = registration;

            using var cts = CancellationTokenSource.CreateLinkedTokenSource(cancellationToken);
            cts.CancelAfter(TimeSpan.FromSeconds(5));

            try
            {
                using (cts.Token.Register(() => tcs.TrySetCanceled()))
                {
                    var result = await tcs.Task.WaitAsync(cts.Token);
                    return result;
                }
            }
            catch (OperationCanceledException)
            {
                _logger.LogWarning("GET_ALL request timed out");
                return Enumerable.Empty<NoteResponseTo>();
            }
            finally
            {
                _pendingAllNotesRequest = null;
            }
        }

        private PendingAllNotesRequest? _pendingAllNotesRequest;

        private class PendingAllNotesRequest
        {
            public TaskCompletionSource<List<NoteResponseTo>> Tcs { get; set; } = null!;
            public CancellationToken CancellationToken { get; set; }
        }

        public void CompleteAllNotesRequest(List<NoteResponseTo> notes)
        {
            _pendingAllNotesRequest?.Tcs.TrySetResult(notes);
        }

        private void AddOrUpdateInternal(NoteResponseTo note)
        {
            _cacheLock.EnterWriteLock();
            try
            {
                var cacheEntry = new CacheEntry<NoteResponseTo>
                {
                    Value = note,
                    LastAccessed = DateTime.UtcNow
                };

                if (_noteCache.TryGetValue(note.Id, out _))
                {
                    _noteCache[note.Id] = cacheEntry;
                    UpdateAccessOrder(note.Id);
                }
                else
                {
                    if (_noteCache.Count >= _maxCacheSize)
                    {
                        EvictLeastRecentlyUsed();
                    }
                    _noteCache[note.Id] = cacheEntry;
                    AddToAccessOrder(note.Id);
                }
            }
            finally
            {
                _cacheLock.ExitWriteLock();
            }
        }

        private NoteResponseTo? GetNoteByIdInternal(long id)
        {
            _cacheLock.EnterUpgradeableReadLock();
            try
            {
                if (_noteCache.TryGetValue(id, out var entry))
                {
                    entry.LastAccessed = DateTime.UtcNow;
                    _cacheLock.EnterWriteLock();
                    try
                    {
                        UpdateAccessOrder(id);
                    }
                    finally
                    {
                        _cacheLock.ExitWriteLock();
                    }
                    return entry.Value;
                }
                return null;
            }
            finally
            {
                _cacheLock.ExitUpgradeableReadLock();
            }
        }

        private IEnumerable<NoteResponseTo> GetAllNotesInternal()
        {
            _cacheLock.EnterReadLock();
            try
            {
                return _noteCache.Values.Select(v => v.Value).ToList();
            }
            finally
            {
                _cacheLock.ExitReadLock();
            }
        }

        private void RemoveNoteInternal(long id)
        {
            _cacheLock.EnterWriteLock();
            try
            {
                _noteCache.TryRemove(id, out _);
                _accessOrder.Remove(id);
            }
            finally
            {
                _cacheLock.ExitWriteLock();
            }
        }

        private void UpdateAccessOrder(long id)
        {
            _accessOrder.Remove(id);
            _accessOrder.AddLast(id);
        }

        private void AddToAccessOrder(long id)
        {
            _accessOrder.AddLast(id);
        }

        private void EvictLeastRecentlyUsed()
        {
            if (_accessOrder.Count == 0) return;
            var lruKey = _accessOrder.First;
            if (lruKey != null)
            {
                _noteCache.TryRemove(lruKey.Value, out _);
                _accessOrder.RemoveFirst();
                _logger.LogDebug("Evicted note {Id} from cache (LRU policy)", lruKey.Value);
            }
        }

        private async Task CleanupExpiredEntries(CancellationToken stoppingToken)
        {
            while (!stoppingToken.IsCancellationRequested)
            {
                try
                {
                    await Task.Delay(_cleanupInterval, stoppingToken);
                    var expiredKeys = new List<long>();

                    _cacheLock.EnterReadLock();
                    try
                    {
                        foreach (var kvp in _noteCache)
                        {
                            if (DateTime.UtcNow - kvp.Value.LastAccessed > _cacheEntryTtl)
                            {
                                expiredKeys.Add(kvp.Key);
                            }
                        }
                    }
                    finally
                    {
                        _cacheLock.ExitReadLock();
                    }

                    if (expiredKeys.Any())
                    {
                        _cacheLock.EnterWriteLock();
                        try
                        {
                            foreach (var key in expiredKeys)
                            {
                                if (_noteCache.TryRemove(key, out _))
                                {
                                    _accessOrder.Remove(key);
                                    _logger.LogDebug("Removed expired note {Id} from cache", key);
                                }
                            }
                        }
                        finally
                        {
                            _cacheLock.ExitWriteLock();
                        }
                        _logger.LogInformation("Cleaned up {Count} expired cache entries", expiredKeys.Count);
                    }
                }
                catch (OperationCanceledException)
                {
                    break;
                }
                catch (Exception ex)
                {
                    _logger.LogError(ex, "Error during cache cleanup");
                }
            }
        }

        // Публичные методы
        public void AddOrUpdateNote(NoteResponseTo note) => AddOrUpdateInternal(note);
        public void RemoveNote(long id) => RemoveNoteInternal(id);
        public NoteResponseTo? GetNoteById(long id) => GetNoteByIdInternal(id);
        public IEnumerable<NoteResponseTo> GetAllNotes() => GetAllNotesInternal();
        public int GetCacheSize() => _noteCache.Count;
        public void ClearCache()
        {
            _cacheLock.EnterWriteLock();
            try
            {
                _noteCache.Clear();
                _accessOrder.Clear();
                _logger.LogInformation("Cache cleared");
            }
            finally
            {
                _cacheLock.ExitWriteLock();
            }
        }

        public override void Dispose()
        {
            _consumer?.Dispose();
            _cacheLock?.Dispose();
            base.Dispose();
        }

        private class CacheEntry<T>
        {
            public T Value { get; set; } = default!;
            public DateTime LastAccessed { get; set; }
        }
    }
}