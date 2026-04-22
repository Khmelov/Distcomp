using Microsoft.AspNetCore.Mvc;
using NewsPortal.Models.Dtos;
using NewsPortal.Models.Dtos.RequestTo;
using NewsPortal.Models.Repositories.Abstractions;
using NewsPortal.Services.Abstractions;

namespace NewsPortal.Controllers
{
    /// <summary>
    /// Контроллер для управления заметками к новостям
    /// </summary>
    [Route("api/v1.0/notes")]
    [ApiController]
    public class NoteController : ControllerBase
    {
        private readonly INoteService _noteService;

        public NoteController(INoteService noteService)
        {
            _noteService = noteService;
        }

        /// <summary>
        /// Получить список всех заметок
        /// </summary>
        /// <returns>Список всех заметок</returns>
        /// <response code="200">Успешное получение списка заметок</response>
        [HttpGet]
        [ProducesResponseType(typeof(IEnumerable<NoteResponseTo>), StatusCodes.Status200OK)]
        public async Task<ActionResult<IEnumerable<NoteResponseTo>>> GetAllNotes()
        {
            var notes = await _noteService.GetAllNotesAsync();
            return Ok(notes);
        }

        /// <summary>
        /// Получить заметку по идентификатору
        /// </summary>
        /// <param name="id">Идентификатор заметки (целое число, больше 0)</param>
        /// <returns>Информация о заметке</returns>
        /// <response code="200">Успешное получение заметки</response>
        /// <response code="400">Некорректный идентификатор (меньше или равен 0)</response>
        /// <response code="404">Заметка с указанным ID не найдена</response>
        [HttpGet("{id}")]
        [ProducesResponseType(typeof(NoteResponseTo), StatusCodes.Status200OK)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status400BadRequest)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status404NotFound)]
        public async Task<ActionResult<NoteResponseTo>> GetNoteById(long id)
        {
            var note = await _noteService.GetNoteByIdAsync(id);
            return Ok(note);
        }

        /// <summary>
        /// Создать новую заметку
        /// </summary>
        /// <param name="noteRequest">Данные для создания заметки</param>
        /// <returns>Созданная заметка</returns>
        /// <response code="201">Заметка успешно создана</response>
        /// <response code="400">
        /// Некорректные данные:
        /// - Content: обязательно, длина от 2 до 2048 символов
        /// - NewsId: положительное число
        /// </response>
        /// <response code="404">Новость с указанным NewsId не найдена</response>
        [HttpPost]
        [ProducesResponseType(typeof(NoteResponseTo), StatusCodes.Status201Created)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status400BadRequest)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status404NotFound)]
        public async Task<ActionResult<NoteResponseTo>> CreateNote([FromBody] NoteRequestTo noteRequest)
        {
            var createdNote = await _noteService.CreateNoteAsync(noteRequest);
            return CreatedAtAction(nameof(GetNoteById), new { id = createdNote.Id }, createdNote);
        }

        /// <summary>
        /// Обновить существующую заметку
        /// </summary>
        /// <param name="noteRequest">Обновленные данные заметки</param>
        /// <returns>Обновленная заметка</returns>
        /// <response code="200">Заметка успешно обновлена</response>
        /// <response code="400">
        /// Некорректные данные:
        /// - ID: больше 0
        /// - Content: длина от 2 до 2048 символов
        /// - NewsId: положительное число
        /// </response>
        /// <response code="404">Заметка или новость с указанным ID не найдены</response>
        [HttpPut]
        [ProducesResponseType(typeof(NoteResponseTo), StatusCodes.Status200OK)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status400BadRequest)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status404NotFound)]
        public async Task<ActionResult<NoteResponseTo>> UpdateNote([FromBody] NoteRequestTo noteRequest)
        {
            await _noteService.UpdateNoteAsync(noteRequest);
            var updatedNote = await _noteService.GetNoteByIdAsync(noteRequest.Id);
            return Ok(updatedNote);
        }

        /// <summary>
        /// Удалить заметку по идентификатору
        /// </summary>
        /// <param name="id">Идентификатор заметки (целое число, больше 0)</param>
        /// <returns>Нет содержимого</returns>
        /// <response code="204">Заметка успешно удалена</response>
        /// <response code="400">Некорректный идентификатор (меньше или равен 0)</response>
        /// <response code="404">Заметка с указанным ID не найдена</response>
        [HttpDelete("{id}")]
        [ProducesResponseType(StatusCodes.Status204NoContent)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status400BadRequest)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status404NotFound)]
        public async Task<ActionResult> DeleteNote(long id)
        {
            await _noteService.DeleteNoteAsync(id);
            return NoContent();
        }

        /// <summary>
        /// Получить заметки с пагинацией
        /// </summary>
        /// <param name="parameters">Параметры пагинации, фильтрации и сортировки</param>
        /// <returns>Список заметок с информацией о пагинации</returns>
        /// <response code="200">Успешное получение списка заметок</response>
        [HttpGet("paged")]
        [ProducesResponseType(typeof(PagedResult<NoteResponseTo>), StatusCodes.Status200OK)]
        public async Task<ActionResult<PagedResult<NoteResponseTo>>> GetPagedNotes([FromQuery] QueryParameters parameters)
        {
            var result = await _noteService.GetPagedNotesAsync(parameters);

            // Добавляем заголовки с информацией о пагинации
            Response.Headers.Add("X-Total-Count", result.TotalCount.ToString());
            Response.Headers.Add("X-Page-Number", result.PageNumber.ToString());
            Response.Headers.Add("X-Page-Size", result.PageSize.ToString());
            Response.Headers.Add("X-Total-Pages", result.TotalPages.ToString());

            return Ok(result);
        }

        /// <summary>
        /// Получить заметки по идентификатору новости
        /// </summary>
        /// <param name="newsId">Идентификатор новости</param>
        /// <returns>Список заметок для указанной новости</returns>
        /// <response code="200">Успешное получение списка заметок</response>
        /// <response code="400">Некорректный идентификатор новости</response>
        /// <response code="404">Новость не найдена</response>
        [HttpGet("by-news/{newsId}")]
        [ProducesResponseType(typeof(IEnumerable<NoteResponseTo>), StatusCodes.Status200OK)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status400BadRequest)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status404NotFound)]
        public async Task<ActionResult<IEnumerable<NoteResponseTo>>> GetNotesByNewsId(long newsId)
        {
            var notes = await _noteService.GetNotesByNewsIdAsync(newsId);
            return Ok(notes);
        }

        /// <summary>
        /// Поиск заметок по содержимому
        /// </summary>
        /// <param name="searchTerm">Текст для поиска</param>
        /// <returns>Список найденных заметок</returns>
        /// <response code="200">Успешный поиск</response>
        [HttpGet("search")]
        [ProducesResponseType(typeof(IEnumerable<NoteResponseTo>), StatusCodes.Status200OK)]
        public async Task<ActionResult<IEnumerable<NoteResponseTo>>> SearchNotes([FromQuery] string searchTerm)
        {
            if (string.IsNullOrWhiteSpace(searchTerm))
            {
                return Ok(new List<NoteResponseTo>());
            }

            var parameters = new QueryParameters
            {
                SearchTerm = searchTerm,
                PageSize = 50
            };

            var result = await _noteService.GetPagedNotesAsync(parameters);
            return Ok(result.Items);
        }

        /// <summary>
        /// Получить последние заметки
        /// </summary>
        /// <param name="count">Количество заметок (по умолчанию 10)</param>
        /// <returns>Список последних заметок</returns>
        /// <response code="200">Успешное получение списка</response>
        [HttpGet("recent")]
        [ProducesResponseType(typeof(IEnumerable<NoteResponseTo>), StatusCodes.Status200OK)]
        public async Task<ActionResult<IEnumerable<NoteResponseTo>>> GetRecentNotes([FromQuery] int count = 10)
        {
            if (count <= 0 || count > 100)
                count = 10;

            var parameters = new QueryParameters
            {
                PageNumber = 1,
                PageSize = count,
                SortBy = "Id",
                SortOrder = "desc"
            };

            var result = await _noteService.GetPagedNotesAsync(parameters);
            return Ok(result.Items);
        }
    }
}