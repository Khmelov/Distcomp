using Microsoft.AspNetCore.Mvc;
using Publisher.src.NewsPortal.Publisher.Application.Dtos;
using Publisher.src.NewsPortal.Publisher.Application.Dtos.RequestTo;
using Publisher.src.NewsPortal.Publisher.Application.Dtos.ResponseTo;
using Publisher.src.NewsPortal.Publisher.Application.Services.Abstractions;
using Publisher.src.NewsPortal.Publisher.Infrastructure.Repositories.Abstractions;

namespace Publisher.src.NewsPortal.Publisher.API.Controllers
{
    /// <summary>
    /// Контроллер для управления создателями контента (авторами)
    /// </summary>
    [Route("api/v1.0/creators")]
    [ApiController]
    public class CreatorController : ControllerBase
    {
        private readonly ICreatorService _creatorService;

        public CreatorController(ICreatorService creatorService)
        {
            _creatorService = creatorService;
        }

        /// <summary>
        /// Получить список всех создателей
        /// </summary>
        /// <returns>Список всех создателей</returns>
        /// <response code="200">Успешное получение списка создателей</response>
        [HttpGet]
        [ProducesResponseType(typeof(IEnumerable<CreatorResponseTo>), StatusCodes.Status200OK)]
        public async Task<ActionResult<IEnumerable<CreatorResponseTo>>> GetAllCreators()
        {
            var creators = await _creatorService.GetAllCreatorsAsync();
            return Ok(creators);
        }

        /// <summary>
        /// Получить создателя по идентификатору
        /// </summary>
        /// <param name="id">Идентификатор создателя (целое число, больше 0)</param>
        /// <returns>Информация о создателе</returns>
        /// <response code="200">Успешное получение создателя</response>
        /// <response code="400">Некорректный идентификатор (меньше или равен 0)</response>
        /// <response code="404">Создатель с указанным ID не найден</response>
        [HttpGet("{id}")]
        [ProducesResponseType(typeof(CreatorResponseTo), StatusCodes.Status200OK)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status400BadRequest)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status404NotFound)]
        public async Task<ActionResult<CreatorResponseTo>> GetCreatorById(long id)
        {
            var creator = await _creatorService.GetCreatorByIdAsync(id);
            return Ok(creator);
        }

        /// <summary>
        /// Создать нового создателя
        /// </summary>
        /// <param name="creatorRequest">Данные для создания создателя</param>
        /// <returns>Созданный создатель</returns>
        /// <response code="201">Создатель успешно создан</response>
        /// <response code="400">
        /// Некорректные данные:
        /// - Login: обязательно, от 2 до 64 символов, только буквы, цифры, '_' и '-'
        /// - Password: обязательно, от 8 до 128 символов
        /// - FirstName: обязательно, от 2 до 64 символов, только буквы
        /// - LastName: обязательно, от 2 до 64 символов, только буквы
        /// </response>
        /// <response code="409">Создатель с таким логином уже существует</response>
        [HttpPost]
        [ProducesResponseType(typeof(CreatorResponseTo), StatusCodes.Status201Created)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status400BadRequest)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status409Conflict)]
        public async Task<ActionResult<CreatorResponseTo>> CreateCreator([FromBody] CreatorRequestTo creatorRequest)
        {
            var createdCreator = await _creatorService.CreateCreatorAsync(creatorRequest);
            return CreatedAtAction(nameof(GetCreatorById), new { id = createdCreator.Id }, createdCreator);
        }

        /// <summary>
        /// Обновить существующего создателя
        /// </summary>
        /// <param name="creatorRequest">Обновленные данные создателя</param>
        /// <returns>Обновленный создатель</returns>
        /// <response code="200">Создатель успешно обновлен</response>
        /// <response code="400">
        /// Некорректные данные:
        /// - ID: больше 0
        /// - Login: от 2 до 64 символов, только буквы, цифры, '_' и '-'
        /// - Password: от 8 до 128 символов
        /// - FirstName: от 2 до 64 символов, только буквы
        /// - LastName: от 2 до 64 символов, только буквы
        /// </response>
        /// <response code="404">Создатель с указанным ID не найден</response>
        /// <response code="409">Создатель с таким логином уже существует</response>
        [HttpPut]
        [ProducesResponseType(typeof(CreatorResponseTo), StatusCodes.Status200OK)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status400BadRequest)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status404NotFound)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status409Conflict)]
        public async Task<ActionResult<CreatorResponseTo>> UpdateCreator([FromBody] CreatorRequestTo creatorRequest)
        {
            await _creatorService.UpdateCreatorAsync(creatorRequest);
            var updatedCreator = await _creatorService.GetCreatorByIdAsync(creatorRequest.Id);
            return Ok(updatedCreator);
        }

        /// <summary>
        /// Удалить создателя по идентификатору
        /// </summary>
        /// <param name="id">Идентификатор создателя (целое число, больше 0)</param>
        /// <returns>Нет содержимого</returns>
        /// <response code="204">Создатель успешно удален</response>
        /// <response code="400">Некорректный идентификатор (меньше или равен 0)</response>
        /// <response code="404">Создатель с указанным ID не найден</response>
        /// <response code="409">Невозможно удалить создателя, так как у него есть связанные новости</response>
        [HttpDelete("{id}")]
        [ProducesResponseType(StatusCodes.Status204NoContent)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status400BadRequest)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status404NotFound)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status409Conflict)]
        public async Task<ActionResult> DeleteCreator(long id)
        {
            await _creatorService.DeleteCreatorAsync(id);
            return NoContent();
        }

        /// <summary>
        /// Получить создателей с пагинацией
        /// </summary>
        /// <param name="parameters">Параметры пагинации, фильтрации и сортировки</param>
        /// <returns>Список создателей с информацией о пагинации</returns>
        /// <response code="200">Успешное получение списка создателей</response>
        [HttpGet("paged")]
        [ProducesResponseType(typeof(PagedResult<CreatorResponseTo>), StatusCodes.Status200OK)]
        public async Task<ActionResult<PagedResult<CreatorResponseTo>>> GetPagedCreators([FromQuery] QueryParameters parameters)
        {
            var result = await _creatorService.GetPagedCreatorsAsync(parameters);

            // Добавляем заголовки с информацией о пагинации
            Response.Headers.Add("X-Total-Count", result.TotalCount.ToString());
            Response.Headers.Add("X-Page-Number", result.PageNumber.ToString());
            Response.Headers.Add("X-Page-Size", result.PageSize.ToString());
            Response.Headers.Add("X-Total-Pages", result.TotalPages.ToString());

            return Ok(result);
        }
    }
}