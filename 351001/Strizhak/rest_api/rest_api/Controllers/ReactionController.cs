using Microsoft.AspNetCore.Mvc;
using rest_api.Dtos;
using rest_api.Entities;
using rest_api.InMemory;
using System.ComponentModel.DataAnnotations;

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
        private readonly ReactionRepository _reactionRepository;
        private readonly ILogger<ReactionController> _logger;

        public ReactionController(ReactionRepository reactionRepository, ILogger<ReactionController> logger)
        {
            _reactionRepository = reactionRepository;
            _logger = logger;
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
            var reaction = _reactionRepository.GetById(id);
            if (reaction == null)
                return NotFound(new { error = $"Reaction with id {id} not found" });

            return Ok(MapToResponse(reaction));
        }

        /// <summary>
        /// Получить все реакции.
        /// </summary>
        /// <returns>Список реакций</returns>
        [HttpGet]
        public ActionResult<IEnumerable<ReactionResponseTo>> GetAll()
        {
            var reactions = _reactionRepository.GetAll();
            return Ok(reactions.Select(MapToResponse));
        }

        /// <summary>
        /// Создать новую реакцию.
        /// </summary>
        /// <param name="reactionRequest">Данные для создания</param>
        /// <returns>Созданная реакция</returns>
        /// <response code="201">Реакция создана</response>
        /// <response code="400">Некорректные данные</response>
        /// <response code="409">Конфликт (например, нарушение уникальности)</response>
        [HttpPost]
        public ActionResult<ReactionResponseTo> Create(ReactionRequestTo reactionRequest)
        {
            var reaction = new Reaction
            {
                TopicId = reactionRequest.TopicId,
                Content = reactionRequest.Content,
               
            };

            try
            {
                _reactionRepository.Add(reaction);
            }
            catch (InvalidOperationException ex)
            {
                return Conflict(new { error = ex.Message });
            }

            var response = MapToResponse(reaction);
            return CreatedAtAction(nameof(GetById), new { id = reaction.Id }, response);
        }

        /// <summary>
        /// Полностью обновить реакцию.
        /// </summary>
        /// <param name="id">Идентификатор реакции</param>
        /// <param name="reactionRequest">Новые данные реакции</param>
        /// <returns>Обновлённая реакция</returns>
        /// <response code="200">Реакция обновлена</response>
        /// <response code="400">Некорректные данные</response>
        /// <response code="404">Реакция не найдена</response>
        /// <response code="409">Конфликт</response>
        [HttpPut("{id:long}")]
        public ActionResult<ReactionResponseTo> Update(long id, ReactionRequestTo reactionRequest)
        {
            var existingReaction = _reactionRepository.GetById(id);
            if (existingReaction == null)
                return NotFound(new { error = $"Reaction with id {id} not found" });

            existingReaction.TopicId = reactionRequest.TopicId;
            existingReaction.Content = reactionRequest.Content;
            

            try
            {
                _reactionRepository.Update(existingReaction);
            }
            catch (InvalidOperationException ex)
            {
                return Conflict(new { error = ex.Message });
            }

            return Ok(MapToResponse(existingReaction));
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
            var reaction = _reactionRepository.GetById(id);
            if (reaction == null)
                return NotFound(new { error = $"Reaction with id {id} not found" });

            _reactionRepository.Delete(id);
            return NoContent();
        }

        /// <summary>
        /// Преобразование сущности Reaction в DTO ответа.
        /// </summary>
        private ReactionResponseTo MapToResponse(Reaction reaction)
        {
            return new ReactionResponseTo
            {
                Id = reaction.Id,
                TopicId = reaction.TopicId,
                Content = reaction.Content,
                
            };
        }
    }
}