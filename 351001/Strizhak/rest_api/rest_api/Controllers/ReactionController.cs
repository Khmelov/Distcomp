using Microsoft.AspNetCore.Mvc;
using rest_api.Dtos;
using rest_api.Entities;
using rest_api.Services;

namespace rest_api.Controllers
{
    /// <summary>
    /// Контроллер для работы с реакциями.
    /// Базовый URL: /api/v1.0/reactions
    /// </summary>
    [ApiController]
    [Route("api/v1.0/reactions")]
    public class ReactionController : ControllerBase
    {
        private readonly IService<Reaction, ReactionRequestTo, ReactionResponseTo> _reactionService;

        public ReactionController(IService<Reaction, ReactionRequestTo, ReactionResponseTo> reactionService)
        {
            _reactionService = reactionService;
        }

        /// <summary>
        /// Получить реакцию по идентификатору.
        /// </summary>
        /// <param name="id">Идентификатор реакции</param>
        /// <returns>Реакция</returns>
        /// <response code="200">Реакция найдена</response>
        /// <response code="404">Реакция не найдена</response>
        [HttpGet("{id:long}")]
        public ActionResult<ReactionResponseTo> GetById(long id)
        {
            var reaction = _reactionService.GetById(id);
            if (reaction == null)
                return NotFound(new { error = $"Reaction with id {id} not found" });
            return Ok(reaction);
        }

        /// <summary>
        /// Получить все реакции.
        /// </summary>
        /// <returns>Список реакций</returns>
        [HttpGet]
        public ActionResult<IEnumerable<ReactionResponseTo>> GetAll()
        {
            var reactions = _reactionService.GetAll();
            return Ok(reactions);
        }

        /// <summary>
        /// Создать новую реакцию.
        /// </summary>
        /// <param name="request">Данные для создания</param>
        /// <returns>Созданная реакция</returns>
        /// <response code="201">Реакция создана</response>
        /// <response code="400">Некорректные данные</response>
        /// <response code="409">Конфликт (например, нарушение уникальности)</response>
        [HttpPost]
        public ActionResult<ReactionResponseTo> Create(ReactionRequestTo request)
        {
            try
            {
                var created = _reactionService.Create(request);
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
        /// Полностью обновить реакцию.
        /// </summary>
        /// <param name="id">Идентификатор реакции (из маршрута)</param>
        /// <param name="request">Новые данные реакции</param>
        /// <returns>Обновлённая реакция</returns>
        /// <response code="200">Реакция обновлена</response>
        /// <response code="400">Некорректные данные</response>
        /// <response code="404">Реакция не найдена</response>
        /// <response code="409">Конфликт</response>
        [HttpPut("{id:long}")]
        public ActionResult<ReactionResponseTo> Update(long id, ReactionRequestTo request)
        {
            // Копируем идентификатор из маршрута в DTO
            request.Id = id;

            try
            {
                var updated = _reactionService.Update(request);
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
        /// Удалить реакцию.
        /// </summary>
        /// <param name="id">Идентификатор реакции</param>
        /// <returns>Статус удаления</returns>
        /// <response code="204">Реакция удалена</response>
        /// <response code="404">Реакция не найдена</response>
        [HttpDelete("{id:long}")]
        public IActionResult Delete(long id)
        {
            try
            {
                _reactionService.Delete(id);
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