using Microsoft.AspNetCore.Mvc;
using rest_api.Dtos;
using rest_api.InMemory;
using System.ComponentModel.DataAnnotations;

namespace rest_api.Controllers
{
    /// <summary>
    /// Контроллер для работы с темами.
    /// Базовый URL: /api/v1.0/topics
    /// </summary>
    [ApiController]
    [Route("api/v1.0/topics")]
    public class TopicsController : ControllerBase
    {
        private readonly TopicRepository _topicRepository;
        private readonly ILogger<TopicsController> _logger; // добавим логгер, как в UserController

        public TopicsController(TopicRepository topicRepository, ILogger<TopicsController> logger)
        {
            _topicRepository = topicRepository;
            _logger = logger;
        }

        /// <summary>
        /// Получить тему по идентификатору.
        /// </summary>
        /// <param name="id">Идентификатор темы</param>
        /// <returns>Тема</returns>
        /// <response code="200">Тема найдена</response>
        /// <response code="404">Тема не найдена</response>
        [HttpGet("{id:long}")]
        public ActionResult<TopicResponseTo> GetById(long id)
        {
            var topic = _topicRepository.GetById(id);
            if (topic == null)
                return NotFound(new { error = $"Topic with id {id} not found" });

            return Ok(MapToResponse(topic));
        }

        /// <summary>
        /// Получить все темы.
        /// </summary>
        /// <returns>Список тем</returns>
        [HttpGet]
        public ActionResult<IEnumerable<TopicResponseTo>> GetAll()
        {
            var topics = _topicRepository.GetAll();
            return Ok(topics.Select(MapToResponse));
        }

        /// <summary>
        /// Создать новую тему.
        /// </summary>
        /// <param name="topicRequest">Данные для создания</param>
        /// <returns>Созданная тема</returns>
        /// <response code="201">Тема создана</response>
        /// <response code="400">Некорректные данные</response>
        /// <response code="409">Конфликт (например, нарушение уникальности)</response>
        [HttpPost]
        public ActionResult<TopicResponseTo> Create(TopicRequestTo topicRequest)
        {
            // Валидация модели выполняется автоматически атрибутом [ApiController]
            var topic = new Topic
            {
                UserId = topicRequest.UserId,
                Title = topicRequest.Title,
                Content = topicRequest.Content,
                Created = DateTime.UtcNow,
                Modified = DateTime.UtcNow
            };

            try
            {
                _topicRepository.Add(topic);
            }
            catch (InvalidOperationException ex)
            {
                return Conflict(new { error = ex.Message });
            }

            var response = MapToResponse(topic);
            return CreatedAtAction(nameof(GetById), new { id = topic.Id }, response);
        }

        /// <summary>
        /// Полностью обновить тему.
        /// </summary>
        /// <param name="topicRequest">Новые данные темы</param>
        /// <returns>Обновлённая тема</returns>
        /// <response code="200">Тема обновлена</response>
        /// <response code="400">Некорректные данные</response>
        /// <response code="404">Тема не найдена</response>
        /// <response code="409">Конфликт</response>
        [HttpPut]
        public ActionResult<TopicResponseTo> Update(TopicRequestTo topicRequest)
        {
            long id = topicRequest.Id;
            var existingTopic = _topicRepository.GetById(id);
            if (existingTopic == null)
                return NotFound(new { error = $"Topic with id {id} not found" });

            // Обновляем поля
            existingTopic.UserId = topicRequest.UserId;
            existingTopic.Title = topicRequest.Title;
            existingTopic.Content = topicRequest.Content;
            existingTopic.Modified = DateTime.UtcNow; 

            try
            {
                _topicRepository.Update(existingTopic);
            }
            catch (InvalidOperationException ex)
            {
                return Conflict(new { error = ex.Message });
            }

            return Ok(MapToResponse(existingTopic));
        }

        /// <summary>
        /// Удалить тему.
        /// </summary>
        /// <param name="id">Идентификатор темы</param>
        /// <returns>Статус удаления</returns>
        /// <response code="204">Тема удалена</response>
        /// <response code="404">Тема не найдена</response>
        [HttpDelete("{id:long}")]
        public IActionResult Delete(long id)
        {
            var topic = _topicRepository.GetById(id);
            if (topic == null)
                return NotFound(new { error = $"Topic with id {id} not found" });

            _topicRepository.Delete(id);
            return NoContent();
        }

        // Преобразование сущности в DTO ответа
        private TopicResponseTo MapToResponse(Topic topic)
        {
            return new TopicResponseTo
            {
                Id = topic.Id,
                UserId = topic.UserId,
                Title = topic.Title,
                Content = topic.Content,
                Created = topic.Created,
                Modified = topic.Modified
            };
        }
    }
}