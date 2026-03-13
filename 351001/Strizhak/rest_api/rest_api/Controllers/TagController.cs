using Microsoft.AspNetCore.Mvc;
using rest_api.Dtos;
using rest_api.Entities;
using rest_api.Services;

namespace rest_api.Controllers
{
    /// <summary>
    /// Контроллер для работы с тегами.
    /// Базовый URL: /api/v1.0/tags
    /// </summary>
    [ApiController]
    [Route("api/v1.0/tags")]
    public class TagController : ControllerBase
    {
        private readonly IService<Tag, TagRequestTo, TagResponseTo> _tagService;

        public TagController(IService<Tag, TagRequestTo, TagResponseTo> tagService)
        {
            _tagService = tagService;
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
            var tag = _tagService.GetById(id);
            if (tag == null)
                return NotFound(new { error = $"Tag with id {id} not found" });
            return Ok(tag);
        }

        /// <summary>
        /// Получить все теги.
        /// </summary>
        /// <returns>Список тегов</returns>
        [HttpGet]
        public ActionResult<IEnumerable<TagResponseTo>> GetAll()
        {
            var tags = _tagService.GetAll();
            return Ok(tags);
        }

        /// <summary>
        /// Создать новый тег.
        /// </summary>
        /// <param name="request">Данные для создания</param>
        /// <returns>Созданный тег</returns>
        /// <response code="201">Тег создан</response>
        /// <response code="400">Некорректные данные</response>
        /// <response code="409">Конфликт (например, тег с таким именем уже существует)</response>
        [HttpPost]
        public ActionResult<TagResponseTo> Create(TagRequestTo request)
        {
            try
            {
                var created = _tagService.Create(request);
                return CreatedAtAction(nameof(GetById), new { id = created.Id }, created);
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
        /// Полностью обновить тег.
        /// </summary>
        /// <param name="id">Идентификатор тега (из маршрута)</param>
        /// <param name="request">Новые данные тега</param>
        /// <returns>Обновлённый тег</returns>
        /// <response code="200">Тег обновлён</response>
        /// <response code="400">Некорректные данные</response>
        /// <response code="404">Тег не найден</response>
        /// <response code="409">Конфликт (например, тег с таким именем уже существует)</response>
        [HttpPut("{id:long}")]
        public ActionResult<TagResponseTo> Update(long id, TagRequestTo request)
        {
            // Копируем идентификатор из маршрута в DTO
            request.Id = id;

            try
            {
                var updated = _tagService.Update(request);
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
        /// Удалить тег.
        /// </summary>
        /// <param name="id">Идентификатор тега</param>
        /// <returns>Статус удаления</returns>
        /// <response code="204">Тег удалён</response>
        /// <response code="404">Тег не найден</response>
        [HttpDelete("{id:long}")]
        public IActionResult Delete(long id)
        {
            try
            {
                _tagService.Delete(id);
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