using Microsoft.AspNetCore.Mvc;
using Publisher.Dtos;
using Publisher.Services;
using System;

namespace Publisher.Controllers
{
    [ApiController]
    [Route("api/v2.0")]
    public class AuthController : BaseApiController
    {
        private readonly IAuthService _authService;

        public AuthController(IAuthService authService)
        {
            _authService = authService;
        }

        /// <summary>
        /// Регистрация нового пользователя
        /// POST /api/v2.0/users
        /// </summary>
        [HttpPost("users")]
        public async Task<IActionResult> Register([FromBody] UserRequestTo request)
        {
            try
            {
                var userResponse = await _authService.RegisterAsync(request);
                // Возвращаем 201 Created с Location и телом ответа
                return CreatedAtAction(nameof(Register), new { id = userResponse.Id }, userResponse);
            }
            catch (InvalidOperationException ex)
            {
                // Логин уже занят → 409 Conflict
                return Error(ex.Message, 409, 1); // errorCode = 40901
            }
            catch (Exception)
            {
                return Error("Registration failed", 500, 1); // errorCode = 50001
            }
        }

        /// <summary>
        /// Аутентификация и получение JWT токена
        /// POST /api/v2.0/login
        /// </summary>
        [HttpPost("login")]
        public async Task<IActionResult> Login([FromBody] LoginRequestTo request)
        {
            try
            {
                var token = await _authService.LoginAsync(request);
                return Ok(new { access_token = token });
            }
            catch (UnauthorizedAccessException ex)
            {
                // Неверный логин/пароль → 401 Unauthorized
                return Error(ex.Message, 401, 1); // errorCode = 40101
            }
            catch (Exception)
            {
                return Error("Login failed", 500, 1); // errorCode = 50001
            }
        }
    }
}