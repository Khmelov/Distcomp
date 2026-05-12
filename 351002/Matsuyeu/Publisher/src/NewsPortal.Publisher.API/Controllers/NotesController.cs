using Discussion.src.NewsPortal.Discussion.Application.Dtos.ResponseTo;
using Microsoft.AspNetCore.Mvc;
using Publisher.src.NewsPortal.Publisher.Application.Dtos;
using Publisher.src.NewsPortal.Publisher.Application.Dtos.RequestTo;
using Publisher.src.NewsPortal.Publisher.Application.Services.Abstractions;
using Publisher.src.NewsPortal.Publisher.Domain.Entities;
using Publisher.src.NewsPortal.Publisher.Infrastructure.Caching;
using Publisher.src.NewsPortal.Publisher.Infrastructure.Messaging;

namespace Publisher.src.NewsPortal.Publisher.API.Controllers
{
    [Route("api/v1.0/notes")]
    [ApiController]
    public class NoteController : ControllerBase
    {
        private readonly IKafkaProducerService _kafkaProducer;
        private readonly ILogger<NoteController> _logger;
        private readonly KafkaConsumerService _kafkaConsumerService;
        private readonly INewsValidationService _newsValidationService;
        private readonly IRedisCacheService _redisCache;

        private const string CACHE_KEY_NOTES_ALL = "notes:all";
        private const string CACHE_KEY_NOTE_PREFIX = "note:";

        public NoteController(
            IKafkaProducerService kafkaProducer,
            ILogger<NoteController> logger,
            KafkaConsumerService kafkaConsumerService,
            INewsValidationService newsValidationService,
            IRedisCacheService redisCache)
        {
            _kafkaProducer = kafkaProducer;
            _logger = logger;
            _kafkaConsumerService = kafkaConsumerService;
            _newsValidationService = newsValidationService;
            _redisCache = redisCache;
        }

        [HttpGet]
        [ProducesResponseType(typeof(IEnumerable<NoteResponseTo>), StatusCodes.Status200OK)]
        public async Task<ActionResult<IEnumerable<NoteResponseTo>>> GetAll()
        {
            //Сначала проверяем In-Memory кеш
            var localNotes = _kafkaConsumerService.GetAllNotes().ToList();
            if (localNotes.Any())
            {
                _logger.LogDebug("GET all: {Count} notes from memory", localNotes.Count);
                return Ok(localNotes);
            }

            //Проверяем Redis кеш
            var areExist = await _redisCache.ExistsAsync(CACHE_KEY_NOTES_ALL);
            if (areExist)
            {
                var redisNotes = await _redisCache.GetAsync<IEnumerable<NoteResponseTo>>(CACHE_KEY_NOTES_ALL);
                if (redisNotes != null && redisNotes.Any())
                {
                    //Фоновое восстановление In-Memory кеша
                    _ = Task.Run(() =>
                    {
                        foreach (var note in redisNotes)
                        {
                            _kafkaConsumerService.AddOrUpdateNote(note);
                        }
                    });
                    _logger.LogDebug("GET all: {Count} notes from Redis", redisNotes.Count());
                    return Ok(redisNotes);
                }
            }
            else
            {
                //Если нет в кешах - отправляем запрос в Kafka
                _logger.LogInformation("No notes in cache, sending GET_ALL request to Kafka");

                var requestMessage = new KafkaNoteMessage
                {
                    Action = "GET_ALL",
                    Data = new Note { Id = 0 }
                };

                await _kafkaProducer.SendAsync("InTopic", requestMessage);

                //Ждем ответ из Kafka (с таймаутом)
                using var cts = new CancellationTokenSource(TimeSpan.FromSeconds(5));
                var timeout = TimeSpan.FromSeconds(5);
                var startTime = DateTime.UtcNow;

                while (DateTime.UtcNow - startTime < timeout)
                {
                    var notesFromKafka = _kafkaConsumerService.GetAllNotes().ToList();
                    if (notesFromKafka.Any())
                    {
                        //Сохраняем в Redis
                        _ = _redisCache.SetAsync(CACHE_KEY_NOTES_ALL, notesFromKafka, TimeSpan.FromMinutes(5));
                        _logger.LogDebug("GET all: {Count} notes from Kafka response", notesFromKafka.Count);
                        return Ok(notesFromKafka);
                    }
                    await Task.Delay(50, cts.Token);
                }

                _logger.LogWarning("GET_ALL request timeout, returning empty list");
                return Ok(new List<NoteResponseTo>());
            }

            return Ok(new List<NoteResponseTo>());
        }

        [HttpGet("{id}")]
        [ProducesResponseType(typeof(NoteResponseTo), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<ActionResult<NoteResponseTo>> GetById(long id)
        {
            //Сначала проверяем In-Memory кеш
            var localNote = _kafkaConsumerService.GetNoteById(id);
            if (localNote != null)
            {
                _logger.LogDebug("GET by id {Id} from memory", id);
                return Ok(localNote);
            }

            //Проверяем Redis кеш
            var cacheKey = $"{CACHE_KEY_NOTE_PREFIX}{id}";
            var isExist = await _redisCache.ExistsAsync(cacheKey);
            if (isExist)
            {
                var redisNote = await _redisCache.GetAsync<NoteResponseTo>(cacheKey);
                if (redisNote != null)
                {
                    _ = Task.Run(() => _kafkaConsumerService.AddOrUpdateNote(redisNote));
                    _logger.LogDebug("GET by id {Id} from Redis", id);
                    return Ok(redisNote);
                }
            }
            else
            {
                //Если нет в кешах - отправляем GET запрос в Kafka
                _logger.LogInformation("Note {Id} not in cache, sending GET request to Kafka", id);

                var requestMessage = new KafkaNoteMessage
                {
                    Action = "GET",
                    Data = new Note { Id = id }
                };

                await _kafkaProducer.SendAsync("InTopic", requestMessage);

                //Ждем ответ из Kafka (с таймаутом)
                using var cts = new CancellationTokenSource(TimeSpan.FromSeconds(5));
                var timeout = TimeSpan.FromSeconds(5);
                var startTime = DateTime.UtcNow;

                while (DateTime.UtcNow - startTime < timeout)
                {
                    var noteFromKafka = _kafkaConsumerService.GetNoteById(id);
                    if (noteFromKafka != null)
                    {
                        //Сохраняем в Redis
                        _ = _redisCache.SetAsync(cacheKey, noteFromKafka, TimeSpan.FromMinutes(10));
                        _logger.LogDebug("GET by id {Id} from Kafka response", id);
                        return Ok(noteFromKafka);
                    }
                    await Task.Delay(50, cts.Token);
                }

                _logger.LogWarning("GET request for note {Id} timed out", id);
                return NotFound(new { errorMessage = $"Note with ID {id} not found", errorCode = "40401" });
            }

            return NotFound(new { errorMessage = $"Note with ID {id} not found", errorCode = "40401" });
        }

        [HttpPost]
        [ProducesResponseType(typeof(NoteResponseTo), StatusCodes.Status201Created)]
        [ProducesResponseType(typeof(ValidationProblemDetails), StatusCodes.Status400BadRequest)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status404NotFound)]
        public async Task<ActionResult<NoteResponseTo>> Create([FromBody] NoteRequestTo request)
        {
            if (!ModelState.IsValid) return BadRequest(ModelState);

            var newsExists = await _newsValidationService.NewsExistsAsync(request.NewsId);
            if (!newsExists)
            {
                return NotFound(new { errorMessage = $"News with ID {request.NewsId} does not exist", errorCode = "40401" });
            }

            var generatedId = DateTime.UtcNow.Ticks;

            //Отправляем в Kafka (асинхронно, не ждем)
            var kafkaMessage = new KafkaNoteMessage
            {
                Action = "CREATE",
                Data = new Note
                {
                    Id = generatedId,
                    NewsId = request.NewsId,
                    Content = request.Content,
                    State = "PENDING"
                }
            };

            //fire-and-forget - не ждем отправки в Kafka
            await _kafkaProducer.SendAsync("InTopic", kafkaMessage);

            var response = new NoteResponseTo
            {
                Id = generatedId,
                NewsId = request.NewsId,
                Content = request.Content,
                State = "PENDING"
            };

            //Быстрое сохранение в память (синхронно, наносекунды)
            _kafkaConsumerService.AddOrUpdateNote(response);

            //Асинхронное сохранение в Redis без ожидания
            _ = _redisCache.SetAsync($"{CACHE_KEY_NOTE_PREFIX}{generatedId}", response, TimeSpan.FromMinutes(10));
            _ = _redisCache.RemoveAsync(CACHE_KEY_NOTES_ALL);

            return CreatedAtAction(nameof(GetById), new { id = generatedId }, response);
        }

        [HttpPut("{id}")]
        [ProducesResponseType(typeof(NoteResponseTo), StatusCodes.Status200OK)]
        [ProducesResponseType(typeof(ValidationProblemDetails), StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<ActionResult<NoteResponseTo>> Update(long id, [FromBody] NoteRequestTo request)
        {
            var existingNote = _kafkaConsumerService.GetNoteById(id);
            if (existingNote == null)
                return NotFound();

            if (!ModelState.IsValid) return BadRequest(ModelState);

            var newsExists = await _newsValidationService.NewsExistsAsync(request.NewsId);
            if (!newsExists)
            {
                return NotFound(new { errorMessage = $"News with ID {request.NewsId} does not exist", errorCode = "40401" });
            }

            //Создаем оптимистичный ответ сразу
            var optimisticResponse = new NoteResponseTo
            {
                Id = id,
                NewsId = request.NewsId,
                Content = request.Content,
                State = existingNote.State
            };

            //Обновляем кеш синхронно 
            _kafkaConsumerService.AddOrUpdateNote(optimisticResponse);

            //Отправляем в Kafka асинхронно
            var kafkaMessage = new KafkaNoteMessage
            {
                Action = "UPDATE",
                Data = new Note
                {
                    Id = id,
                    NewsId = request.NewsId,
                    Content = request.Content,
                    State = existingNote.State
                }
            };

            await _kafkaProducer.SendAsync("InTopic", kafkaMessage);

            //Асинхронное обновление Redis без ожидания
            _ = _redisCache.SetAsync($"{CACHE_KEY_NOTE_PREFIX}{id}", optimisticResponse, TimeSpan.FromMinutes(10));
            _ = _redisCache.RemoveAsync(CACHE_KEY_NOTES_ALL);

            //Возвращаем ответ немедленно
            return Ok(optimisticResponse);
        }

        [HttpDelete("{id}")]
        [ProducesResponseType(StatusCodes.Status204NoContent)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<IActionResult> Delete(long id)
        {
            var existingNote = _kafkaConsumerService.GetNoteById(id);
            if (existingNote == null)
                return NotFound();

            //Отправляем в Kafka (fire-and-forget)
            var kafkaMessage = new KafkaNoteMessage
            {
                Action = "DELETE",
                Data = new Note
                {
                    Id = id,
                    NewsId = existingNote.NewsId
                }
            };

            await _kafkaProducer.SendAsync("InTopic", kafkaMessage);

            _kafkaConsumerService.RemoveNote(id);
            _ = _redisCache.RemoveAsync($"{CACHE_KEY_NOTE_PREFIX}{id}");
            _ = _redisCache.RemoveAsync(CACHE_KEY_NOTES_ALL);

            return NoContent();
        }
    }
}