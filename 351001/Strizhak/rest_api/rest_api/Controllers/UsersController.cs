using Microsoft.AspNetCore.Mvc;
using rest_api.Dtos;
using rest_api.Entities;
using rest_api.Services;

namespace rest_api.Controllers
{
    /// <summary>
    /// Контроллер для работы с пользователями.
    /// Базовый URL: /api/v1.0/users
    /// </summary>
    [ApiController]
    [Route("api/v1.0/users")]
    public class UsersController : ControllerBase
    {
        private readonly IService<User, UserRequestTo, UserResponseTo> _userService;

        public UsersController(IService<User, UserRequestTo, UserResponseTo> userService)
        {
            _userService = userService;
        }

        /// <summary>
        /// Получить пользователя по идентификатору.
        /// </summary>
        /// <param name="id">Идентификатор пользователя</param>
        /// <returns>Пользователь (без пароля)</returns>
        /// <response code="200">Пользователь найден</response>
        /// <response code="404">Пользователь не найден</response>
        [HttpGet("{id:long}")]
        public ActionResult<UserResponseTo> GetById(long id)
        {
            var user = _userService.GetById(id);
            if (user == null)
                return NotFound(new { error = $"User with id {id} not found" });

            return Ok(user);
        }

        /// <summary>
        /// Получить всех пользователей.
        /// </summary>
        /// <returns>Список пользователей</returns>
        [HttpGet]
        public ActionResult<IEnumerable<UserResponseTo>> GetAll()
        {
            var users = _userService.GetAll();
            return Ok(users);
        }

        /// <summary>
        /// Создать нового пользователя.
        /// </summary>
        /// <param name="request">Данные для создания</param>
        /// <returns>Созданный пользователь</returns>
        /// <response code="201">Пользователь создан</response>
        /// <response code="400">Некорректные данные</response>
        /// <response code="409">Конфликт (например, логин уже существует)</response>
        [HttpPost]
        public ActionResult<UserResponseTo> Create(UserRequestTo request)
        {
            try
            {
                var created = _userService.Create(request);
                return CreatedAtAction(nameof(GetById), new { id = created.Id }, created);
            }
            catch (InvalidOperationException ex)
            {
                // Конфликт (например, дубликат логина)
                return Conflict(new { error = ex.Message });
            }
            catch (Exception ex)
            {
                // Непредвиденная ошибка
                return StatusCode(500, new { error = "Internal server error" });
            }
        }

        /// <summary>
        /// Полностью обновить пользователя.
        /// </summary>
        /// <param name="request">Новые данные пользователя (включая Id)</param>
        /// <returns>Обновлённый пользователь</returns>
        /// <response code="200">Пользователь обновлён</response>
        /// <response code="400">Некорректные данные</response>
        /// <response code="404">Пользователь не найден</response>
        /// <response code="409">Конфликт (например, логин уже занят)</response>
        [HttpPut]
        public ActionResult<UserResponseTo> Update(UserRequestTo request)
        {
            try
            {
                var updated = _userService.Update(request);
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
        /// Удалить пользователя.
        /// </summary>
        /// <param name="id">Идентификатор пользователя</param>
        /// <returns>Статус удаления</returns>
        /// <response code="204">Пользователь удалён</response>
        /// <response code="404">Пользователь не найден</response>
        [HttpDelete("{id:long}")]
        public IActionResult Delete(long id)
        {
            try
            {
                _userService.Delete(id);
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