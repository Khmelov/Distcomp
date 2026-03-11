using Microsoft.AspNetCore.Mvc;
using rest_api.Dtos;
using rest_api.InMemory;
using BCrypt.Net;
using System.ComponentModel.DataAnnotations;

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
        private readonly UserRepository _userRepository;
       // private readonly ILogger<UsersController> _logger;

        public UsersController(UserRepository userRepository/* ILogger<UsersController> logger*/)
        {
            _userRepository = userRepository;
            //_logger = logger;
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
            var user = _userRepository.GetById(id);
            if (user == null)
                return NotFound(new { error = $"User with id {id} not found" });

            return Ok(MapToResponse(user));
        }

        /// <summary>
        /// Получить всех пользователей.
        /// </summary>
        /// <returns>Список пользователей</returns>
        [HttpGet]
        public ActionResult<IEnumerable<UserResponseTo>> GetAll()
        {
            var users = _userRepository.GetAll();
            return Ok(users.Select(MapToResponse));
        }

        /// <summary>
        /// Создать нового пользователя.
        /// </summary>
        /// <param name="userRequest">Данные для создания</param>
        /// <returns>Созданный пользователь</returns>
        /// <response code="201">Пользователь создан</response>
        /// <response code="400">Некорректные данные</response>
        /// <response code="409">Конфликт (например, логин уже существует)</response>
        [HttpPost]
        public ActionResult<UserResponseTo> Create(UserRequestTo userRequest)
        {
            var user = new User
            {
                Login = userRequest.Login,
                Password = HashPassword(userRequest.Password),
                Firstname = userRequest.Firstname,
                Lastname = userRequest.Lastname
            };

            try
            {
                _userRepository.Add(user);
            }
            catch (InvalidOperationException ex) 
            {
                return Conflict(new { error = ex.Message });
            }

            var response = MapToResponse(user);
            return CreatedAtAction(nameof(GetById), new { id = user.Id }, response);
        }

        /// <summary>
        /// Полностью обновить пользователя.
        /// </summary>
        /// <param name="userRequest">Новые данные пользователя</param>
        /// <returns>Обновлённый пользователь</returns>
        /// <response code="200">Пользователь обновлён</response>
        /// <response code="400">Некорректные данные</response>
        /// <response code="404">Пользователь не найден</response>
        /// <response code="409">Конфликт (например, логин уже занят)</response>
        [HttpPut]
        public ActionResult<UserResponseTo> Update(UserRequestTo userRequest)
        {
            long id = userRequest.Id;
            var existingUser = _userRepository.GetById(id);
            if (existingUser == null)
                return NotFound(new { error = $"User with id {id} not found" });

            // Обновляем поля
            existingUser.Login = userRequest.Login;
            existingUser.Password = HashPassword(userRequest.Password);
            existingUser.Firstname = userRequest.Firstname;
            existingUser.Lastname = userRequest.Lastname;

            try
            {
                _userRepository.Update(existingUser);
            }
            catch (InvalidOperationException ex) 
            {
                return Conflict(new { error = ex.Message });
            }

            return Ok(MapToResponse(existingUser));
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
            var user = _userRepository.GetById(id);
            if (user == null)
                return NotFound(new { error = $"User with id {id} not found" });

            _userRepository.Delete(id);
            return NoContent();
        }
        private UserResponseTo MapToResponse(User user)
        {
            return new UserResponseTo
            {
                Id = user.Id,
                Login = user.Login,
                Firstname = user.Firstname,
                Lastname = user.Lastname
            };
        }

        private string HashPassword(string password) => BCrypt.Net.BCrypt.HashPassword(password);
    }
}