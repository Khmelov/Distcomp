using Microsoft.AspNetCore.Mvc;
using rest_api.Dtos;
using rest_api.Entities;
using rest_api.Services;

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
        private readonly IService<Topic, TopicRequestTo, TopicResponseTo> _topicService;

        public TopicsController(IService<Topic, TopicRequestTo, TopicResponseTo> topicService)
        {
            _topicService = topicService;
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
            var topic = _topicService.GetById(id);
            if (topic == null)
                return NotFound(new { error = $"Topic with id {id} not found" });
            return Ok(topic);
        }

        /// <summary>
        /// Получить все темы.
        /// </summary>
        /// <returns>Список тем</returns>
        [HttpGet]
        public ActionResult<IEnumerable<TopicResponseTo>> GetAll()
        {
            var topics = _topicService.GetAll();
            return Ok(topics);
        }

        /// <summary>
        /// Создать новую тему.
        /// </summary>
        /// <param name="request">Данные для создания</param>
        /// <returns>Созданная тема</returns>
        /// <response code="201">Тема создана</response>
        /// <response code="400">Некорректные данные</response>
        /// <response code="409">Конфликт (например, нарушение уникальности)</response>
        [HttpPost]
        public ActionResult<TopicResponseTo> Create(TopicRequestTo request)
        {
            try
            {
                var created = _topicService.Create(request);
                return CreatedAtAction(nameof(GetById), new { id = created.Id }, created);
            }
            catch (InvalidOperationException ex)
            {
                return Conflict(new { error = ex.Message });
            }
            catch (Exception ex)
            {
                // Логирование ошибки
                return StatusCode(500, new { error = "Internal server error" });
            }
        }

        /// <summary>
        /// Полностью обновить тему.
        /// </summary>
        /// <param name="id">Идентификатор темы (из маршрута)</param>
        /// <param name="request">Новые данные темы</param>
        /// <returns>Обновлённая тема</returns>
        /// <response code="200">Тема обновлена</response>
        /// <response code="400">Некорректные данные</response>
        /// <response code="404">Тема не найдена</response>
        /// <response code="409">Конфликт</response>
        [HttpPut]
        public ActionResult<TopicResponseTo> Update(TopicRequestTo request)
        {
            // Копируем идентификатор из маршрута в DTO
            var id = request.Id;
            

            try
            {
                var updated = _topicService.Update(request);
                return Ok(updated);
            }
            catch (KeyNotFoundException ex)
            {
                return NotFound(new { error = ex.Message });
            }
            catch (InvalidOperationException ex)
            {
                return Conflict(new { error = ex.Message });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { error = "Internal server error" });
            }
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
            try
            {
                _topicService.Delete(id);
                return NoContent();
            }
            catch (KeyNotFoundException ex)
            {
                return NotFound(new { error = ex.Message });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { error = "Internal server error" });
            }
        }
    }
}