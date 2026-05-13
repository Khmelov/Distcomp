using Microsoft.AspNetCore.Mvc;
using Publisher.src.NewsPortal.Publisher.Application.Dtos;
using Publisher.src.NewsPortal.Publisher.Application.Dtos.RequestTo;
using Publisher.src.NewsPortal.Publisher.Application.Dtos.ResponseTo;
using Publisher.src.NewsPortal.Publisher.Application.Services.Abstractions;
using Publisher.src.NewsPortal.Publisher.Infrastructure.Repositories.Abstractions;

namespace Publisher.src.NewsPortal.Publisher.API.Controllers
{
    /// <summary>
    /// Контроллер для управления новостями
    /// </summary>
    [Route("api/v1.0/news")]
    [ApiController]
    public class NewsController : ControllerBase
    {
        private readonly INewsService _newsService;

        public NewsController(INewsService newsService)
        {
            _newsService = newsService;
        }

        /// <summary>
        /// Получить список всех новостей
        /// </summary>
        /// <returns>Список всех новостей</returns>
        /// <response code="200">Успешное получение списка новостей</response>
        [HttpGet]
        [ProducesResponseType(typeof(IEnumerable<NewsResponseTo>), StatusCodes.Status200OK)]
        public async Task<ActionResult<IEnumerable<NewsResponseTo>>> GetAllNews()
        {
            var news = await _newsService.GetAllNewsAsync();
            return Ok(news);
        }

        /// <summary>
        /// Получить новость по идентификатору
        /// </summary>
        /// <param name="id">Идентификатор новости (целое число, больше 0)</param>
        /// <returns>Информация о новости</returns>
        /// <response code="200">Успешное получение новости</response>
        /// <response code="400">Некорректный идентификатор (меньше или равен 0)</response>
        /// <response code="404">Новость с указанным ID не найдена</response>
        [HttpGet("{id}")]
        [ProducesResponseType(typeof(NewsResponseTo), StatusCodes.Status200OK)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status400BadRequest)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status404NotFound)]
        public async Task<ActionResult<NewsResponseTo>> GetNewsById(long id)
        {
            var news = await _newsService.GetNewsByIdAsync(id);
            return Ok(news);
        }

        /// <summary>
        /// Создать новую новость
        /// </summary>
        /// <param name="newsRequest">Данные для создания новости</param>
        /// <returns>Созданная новость</returns>
        /// <response code="201">Новость успешно создана</response>
        /// <response code="400">
        /// Некорректные данные:
        /// - Title: обязательно, длина от 2 до 64 символов
        /// - Content: обязательно, длина от 4 до 2048 символов
        /// - CreatorId: положительное число
        /// </response>
        /// <response code="404">Создатель с указанным CreatorId не найден</response>
        /// <response code="409">Новость с таким заголовком уже существует</response>
        [HttpPost]
        [ProducesResponseType(typeof(NewsResponseTo), StatusCodes.Status201Created)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status400BadRequest)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status404NotFound)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status409Conflict)]
        public async Task<ActionResult<NewsResponseTo>> CreateNews([FromBody] NewsRequestTo newsRequest)
        {
            var createdNews = await _newsService.CreateNewsAsync(newsRequest);
            return CreatedAtAction(nameof(GetNewsById), new { id = createdNews.Id }, createdNews);
        }

        /// <summary>
        /// Обновить существующую новость
        /// </summary>
        /// <param name="newsRequest">Обновленные данные новости</param>
        /// <returns>Обновленная новость</returns>
        /// <response code="200">Новость успешно обновлена</response>
        /// <response code="400">
        /// Некорректные данные:
        /// - ID: больше 0
        /// - Title: длина от 2 до 64 символов
        /// - Content: длина от 4 до 2048 символов
        /// - CreatorId: положительное число
        /// </response>
        /// <response code="404">Новость или создатель с указанным ID не найдены</response>
        /// <response code="409">Новость с таким заголовком уже существует</response>
        [HttpPut]
        [ProducesResponseType(typeof(NewsResponseTo), StatusCodes.Status200OK)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status400BadRequest)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status404NotFound)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status409Conflict)]
        public async Task<ActionResult<NewsResponseTo>> UpdateNews([FromBody] NewsRequestTo newsRequest)
        {
            await _newsService.UpdateNewsAsync(newsRequest);
            var updatedNews = await _newsService.GetNewsByIdAsync(newsRequest.Id);
            return Ok(updatedNews);
        }

        /// <summary>
        /// Удалить новость по идентификатору
        /// </summary>
        /// <param name="id">Идентификатор новости (целое число, больше 0)</param>
        /// <returns>Нет содержимого</returns>
        /// <response code="204">Новость успешно удалена</response>
        /// <response code="400">Некорректный идентификатор (меньше или равен 0)</response>
        /// <response code="404">Новость с указанным ID не найдена</response>
        [HttpDelete("{id}")]
        [ProducesResponseType(StatusCodes.Status204NoContent)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status400BadRequest)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status404NotFound)]
        public async Task<ActionResult> DeleteNews(long id)
        {
            await _newsService.DeleteNewsAsync(id);
            return NoContent();
        }

        /// <summary>
        /// Получить новости с пагинацией
        /// </summary>
        /// <param name="parameters">Параметры пагинации, фильтрации и сортировки</param>
        /// <returns>Список новостей с информацией о пагинации</returns>
        /// <response code="200">Успешное получение списка новостей</response>
        [HttpGet("paged")]
        [ProducesResponseType(typeof(PagedResult<NewsResponseTo>), StatusCodes.Status200OK)]
        public async Task<ActionResult<PagedResult<NewsResponseTo>>> GetPagedNews([FromQuery] QueryParameters parameters)
        {
            var result = await _newsService.GetPagedNewsAsync(parameters);

            // Добавляем заголовки с информацией о пагинации
            Response.Headers.Add("X-Total-Count", result.TotalCount.ToString());
            Response.Headers.Add("X-Page-Number", result.PageNumber.ToString());
            Response.Headers.Add("X-Page-Size", result.PageSize.ToString());
            Response.Headers.Add("X-Total-Pages", result.TotalPages.ToString());

            return Ok(result);
        }

        /// <summary>
        /// Получить новости по идентификатору создателя
        /// </summary>
        /// <param name="creatorId">Идентификатор создателя</param>
        /// <returns>Список новостей создателя</returns>
        /// <response code="200">Успешное получение списка новостей</response>
        /// <response code="400">Некорректный идентификатор создателя</response>
        /// <response code="404">Создатель не найден</response>
        [HttpGet("by-creator/{creatorId}")]
        [ProducesResponseType(typeof(IEnumerable<NewsResponseTo>), StatusCodes.Status200OK)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status400BadRequest)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status404NotFound)]
        public async Task<ActionResult<IEnumerable<NewsResponseTo>>> GetNewsByCreatorId(long creatorId)
        {
            var news = await _newsService.GetNewsByCreatorIdAsync(creatorId);
            return Ok(news);
        }

        /// <summary>
        /// Получить новости по идентификатору метки
        /// </summary>
        /// <param name="markName">Идентификатор метки</param>
        /// <returns>Список новостей с указанной меткой</returns>
        /// <response code="200">Успешное получение списка новостей</response>
        /// <response code="400">Некорректный идентификатор метки</response>
        /// <response code="404">Метка не найдена</response>
        [HttpGet("by-mark/{markId}")]
        [ProducesResponseType(typeof(IEnumerable<NewsResponseTo>), StatusCodes.Status200OK)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status400BadRequest)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status404NotFound)]
        public async Task<ActionResult<IEnumerable<NewsResponseTo>>> GetNewsByMarkId(string markName)
        {
            var news = await _newsService.GetNewsByMarkNameAsync(markName);
            return Ok(news);
        }
    }
}