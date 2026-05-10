using Discussion.src.NewsPortal.Discussion.Application.Dtos.ResponseTo;
using Microsoft.AspNetCore.Mvc;
using Publisher.src.NewsPortal.Publisher.Application.Dtos;
using Publisher.src.NewsPortal.Publisher.Application.Dtos.RequestTo;
using Publisher.src.NewsPortal.Publisher.Application.Services.Abstractions;

namespace Publisher.src.NewsPortal.Publisher.API.Controllers
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
        /// Обновить существующую заметку (ID в строке запроса)
        /// </summary>
        /// <param name="id">Идентификатор заметки (целое число, больше 0)</param>
        /// <param name="noteRequest">Обновленные данные заметки (без ID)</param>
        /// <returns>Обновленная заметка</returns>
        /// <response code="200">Заметка успешно обновлена</response>
        /// <response code="400">
        /// Некорректные данные:
        /// - ID: больше 0
        /// - Content: длина от 2 до 2048 символов
        /// - NewsId: положительное число
        /// </response>
        /// <response code="404">Заметка с указанным ID не найдена</response>
        [HttpPut("{id}")]
        [ProducesResponseType(typeof(NoteResponseTo), StatusCodes.Status200OK)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status400BadRequest)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status404NotFound)]
        public async Task<ActionResult<NoteResponseTo>> UpdateNoteById(long id, [FromBody] NoteRequestTo noteRequest)
        {
            noteRequest.Id = id;

            await _noteService.UpdateNoteAsync(noteRequest);
            var updatedNote = await _noteService.GetNoteByIdAsync(id);
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
    }
}