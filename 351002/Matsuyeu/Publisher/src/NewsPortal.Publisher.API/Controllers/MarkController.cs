using Microsoft.AspNetCore.Mvc;
using Publisher.src.NewsPortal.Publisher.Application.Dtos;
using Publisher.src.NewsPortal.Publisher.Application.Dtos.RequestTo;
using Publisher.src.NewsPortal.Publisher.Application.Dtos.ResponseTo;
using Publisher.src.NewsPortal.Publisher.Application.Services.Abstractions;
using Publisher.src.NewsPortal.Publisher.Infrastructure.Repositories.Abstractions;

namespace Publisher.src.NewsPortal.Publisher.API.Controllers
{
    /// <summary>
    /// Контроллер для управления метками (тегами) новостей
    /// </summary>
    [Route("api/v1.0/marks")]
    [ApiController]
    public class MarkController : ControllerBase
    {
        private readonly IMarkService _markService;

        public MarkController(IMarkService markService)
        {
            _markService = markService;
        }

        /// <summary>
        /// Получить список всех меток
        /// </summary>
        /// <returns>Список всех меток</returns>
        /// <response code="200">Успешное получение списка меток</response>
        [HttpGet]
        [ProducesResponseType(typeof(IEnumerable<MarkResponseTo>), StatusCodes.Status200OK)]
        public async Task<ActionResult<IEnumerable<MarkResponseTo>>> GetAllMarks()
        {
            var marks = await _markService.GetAllMarksAsync();
            return Ok(marks);
        }

        /// <summary>
        /// Получить метку по идентификатору
        /// </summary>
        /// <param name="id">Идентификатор метки (целое число, больше 0)</param>
        /// <returns>Информация о метке</returns>
        /// <response code="200">Успешное получение метки</response>
        /// <response code="400">Некорректный идентификатор (меньше или равен 0)</response>
        /// <response code="404">Метка с указанным ID не найдена</response>
        [HttpGet("{id}")]
        [ProducesResponseType(typeof(MarkResponseTo), StatusCodes.Status200OK)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status400BadRequest)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status404NotFound)]
        public async Task<ActionResult<MarkResponseTo>> GetMarkById(long id)
        {
            var mark = await _markService.GetMarkByIdAsync(id);
            return Ok(mark);
        }

        /// <summary>
        /// Создать новую метку
        /// </summary>
        /// <param name="markRequest">Данные для создания метки</param>
        /// <returns>Созданная метка</returns>
        /// <response code="201">Метка успешно создана</response>
        /// <response code="400">Некорректные данные (имя обязательно, длина от 2 до 32 символов)</response>
        /// <response code="409">Метка с таким именем уже существует</response>
        [HttpPost]
        [ProducesResponseType(typeof(MarkResponseTo), StatusCodes.Status201Created)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status400BadRequest)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status409Conflict)]
        public async Task<ActionResult<MarkResponseTo>> CreateMark([FromBody] MarkRequestTo markRequest)
        {
            var createdMark = await _markService.CreateMarkAsync(markRequest);
            return CreatedAtAction(nameof(GetMarkById), new { id = createdMark.Id }, createdMark);
        }

        /// <summary>
        /// Обновить существующую метку
        /// </summary>
        /// <param name="markRequest">Обновленные данные метки</param>
        /// <returns>Обновленная метка</returns>
        /// <response code="200">Метка успешно обновлена</response>
        /// <response code="400">Некорректные данные (ID больше 0, имя от 2 до 32 символов)</response>
        /// <response code="404">Метка с указанным ID не найдена</response>
        /// <response code="409">Метка с таким именем уже существует</response>
        [HttpPut]
        [ProducesResponseType(typeof(MarkResponseTo), StatusCodes.Status200OK)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status400BadRequest)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status404NotFound)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status409Conflict)]
        public async Task<ActionResult<MarkResponseTo>> UpdateMark([FromBody] MarkRequestTo markRequest)
        {
            await _markService.UpdateMarkAsync(markRequest);
            var updatedMark = await _markService.GetMarkByIdAsync(markRequest.Id);
            return Ok(updatedMark);
        }

        /// <summary>
        /// Удалить метку по идентификатору
        /// </summary>
        /// <param name="id">Идентификатор метки (целое число, больше 0)</param>
        /// <returns>Нет содержимого</returns>
        /// <response code="204">Метка успешно удалена</response>
        /// <response code="400">Некорректный идентификатор (меньше или равен 0)</response>
        /// <response code="404">Метка с указанным ID не найдена</response>
        [HttpDelete("{id}")]
        [ProducesResponseType(StatusCodes.Status204NoContent)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status400BadRequest)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status404NotFound)]
        public async Task<ActionResult> DeleteMark(long id)
        {
            await _markService.DeleteMarkAsync(id);
            return NoContent();
        }

        /// <summary>
        /// Получить метки с пагинацией
        /// </summary>
        /// <param name="parameters">Параметры пагинации, фильтрации и сортировки</param>
        /// <returns>Список меток с информацией о пагинации</returns>
        /// <response code="200">Успешное получение списка меток</response>
        [HttpGet("paged")]
        [ProducesResponseType(typeof(PagedResult<MarkResponseTo>), StatusCodes.Status200OK)]
        public async Task<ActionResult<PagedResult<MarkResponseTo>>> GetPagedMarks([FromQuery] QueryParameters parameters)
        {
            var result = await _markService.GetPagedMarksAsync(parameters);

            // Добавляем заголовки с информацией о пагинации
            Response.Headers.Add("X-Total-Count", result.TotalCount.ToString());
            Response.Headers.Add("X-Page-Number", result.PageNumber.ToString());
            Response.Headers.Add("X-Page-Size", result.PageSize.ToString());
            Response.Headers.Add("X-Total-Pages", result.TotalPages.ToString());

            return Ok(result);
        }
    }
}