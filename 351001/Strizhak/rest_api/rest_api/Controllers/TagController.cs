using Microsoft.AspNetCore.Mvc;
using rest_api.Dtos;
using rest_api.Entities;
using rest_api.InMemory;
using System.ComponentModel.DataAnnotations;

namespace rest_api.Controllers
{
    /// <summary>
    /// Контроллер для работы с тегами.
    /// Базовый URL: /api/v1.0/tags
    /// </summary>
    [Route("api/v1.0/tags")]
    [ApiController]
    public class TagController : ControllerBase
    {
        private readonly TagRepository _tagRepository;
        private readonly ILogger<TagController> _logger;

        public TagController(TagRepository tagRepository, ILogger<TagController> logger)
        {
            _tagRepository = tagRepository;
            _logger = logger;
        }

        /// <summary>
        /// Получить тег по идентификатору.
        /// </summary>
        /// <param name="id">Идентификатор тега</param>
        /// <returns>Тег</returns>
        /// <response code="200">Тег найден</response>
        /// <response code="404">Тег не найден</response>
        [HttpGet("{id:long}")]
        public ActionResult<TagResponseTo> GetById(long id)
        {
            var tag = _tagRepository.GetById(id);
            if (tag == null)
                return NotFound(new { error = $"Tag with id {id} not found" });

            return Ok(MapToResponse(tag));
        }

        /// <summary>
        /// Получить все теги.
        /// </summary>
        /// <returns>Список тегов</returns>
        [HttpGet]
        public ActionResult<IEnumerable<TagResponseTo>> GetAll()
        {
            var tags = _tagRepository.GetAll();
            return Ok(tags.Select(MapToResponse));
        }

        /// <summary>
        /// Создать новый тег.
        /// </summary>
        /// <param name="tagRequest">Данные для создания</param>
        /// <returns>Созданный тег</returns>
        /// <response code="201">Тег создан</response>
        /// <response code="400">Некорректные данные</response>
        /// <response code="409">Конфликт (например, тег с таким именем уже существует)</response>
        [HttpPost]
        public ActionResult<TagResponseTo> Create(TagRequestTo tagRequest)
        {
            var tag = new Tag
            {
                Name = tagRequest.Name
                // Id будет сгенерирован репозиторием (например, автоинкремент)
            };

            try
            {
                _tagRepository.Add(tag);
            }
            catch (InvalidOperationException ex)
            {
                // Логируем исключение
                _logger.LogWarning(ex, "Conflict while creating tag");
                return Conflict(new { error = ex.Message });
            }

            var response = MapToResponse(tag);
            return CreatedAtAction(nameof(GetById), new { id = tag.Id }, response);
        }

        /// <summary>
        /// Полностью обновить тег.
        /// </summary>
        /// <param name="id">Идентификатор тега</param>
        /// <param name="tagRequest">Новые данные тега</param>
        /// <returns>Обновлённый тег</returns>
        /// <response code="200">Тег обновлён</response>
        /// <response code="400">Некорректные данные</response>
        /// <response code="404">Тег не найден</response>
        /// <response code="409">Конфликт (например, тег с таким именем уже существует)</response>
        [HttpPut("{id:long}")]
        public ActionResult<TagResponseTo> Update(long id, TagRequestTo tagRequest)
        {
            var existingTag = _tagRepository.GetById(id);
            if (existingTag == null)
                return NotFound(new { error = $"Tag with id {id} not found" });

            existingTag.Name = tagRequest.Name;

            try
            {
                _tagRepository.Update(existingTag);
            }
            catch (InvalidOperationException ex)
            {
                _logger.LogWarning(ex, "Conflict while updating tag");
                return Conflict(new { error = ex.Message });
            }

            return Ok(MapToResponse(existingTag));
        }

        /// <summary>
        /// Удалить тег.
        /// </summary>
        /// <param name="id">Идентификатор тега</param>
        /// <returns>Статус удаления</returns>
        /// <response code="204">Тег удалён</response>
        /// <response code="404">Тег не найден</response>
        [HttpDelete("{id:long}")]
        public IActionResult Delete(long id)
        {
            var tag = _tagRepository.GetById(id);
            if (tag == null)
                return NotFound(new { error = $"Tag with id {id} not found" });

            _tagRepository.Delete(id);
            return NoContent();
        }

        /// <summary>
        /// Преобразование сущности Tag в DTO ответа.
        /// </summary>
        private TagResponseTo MapToResponse(Tag tag)
        {
            return new TagResponseTo
            {
                Id = tag.Id,
                Name = tag.Name
            };
        }
    }
}