using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Redis.Data;
using Redis.Models;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;

namespace Redis.Controllers.V2;

// 2.0 обязательная проверка безопасности
[ApiController]
[Route("api/v2.0/creators")]
public class CreatorV2Controller : ControllerBase
{
    private readonly PublisherDbContext _context;

    public CreatorV2Controller(PublisherDbContext context) { _context = context; }

    // Получение всех авторов. Доступ только для авторизованных пользователей (с токеном)
    [HttpGet]
    [Authorize]
    public async Task<IActionResult> GetAll() => Ok(await _context.Creators.ToListAsync());

    // Получение конкретного автора. Доступ только по токену
    [HttpGet("{id}")]
    [Authorize]
    public async Task<IActionResult> Get(int id)
    {
        var creator = await _context.Creators.FindAsync(id);
        if (creator == null) return NotFound();
        return Ok(creator);
    }

    // Регистрация нового автора. 
    [HttpPost]
    [AllowAnonymous]
    public async Task<IActionResult> Register([FromBody] Creator creator)
    {
        // Проверка уникальности логина 
        if (await _context.Creators.AnyAsync(c => c.Login == creator.Login))
            return BadRequest(new ErrorResponse { errorMessage = "Login exists", errorCode = "40001" });

        // BCrypt преобразует пароль в защищенный хэш. 
        creator.Password = BCrypt.Net.BCrypt.HashPassword(creator.Password, 4);
        
        // Если роль не указана, по умолчанию ставим CUSTOMER
        if (string.IsNullOrEmpty(creator.Role)) creator.Role = "CUSTOMER";

        _context.Creators.Add(creator);
        await _context.SaveChangesAsync();
        
        // Возвращаем 201 Created — успешная регистрация
        return StatusCode(201, creator);
    }

    // Обновление данных автора. Требует токен.
    [HttpPut("{id}")]
    [Authorize]
    public async Task<IActionResult> Update(int id, [FromBody] Creator creator)
    {
        var existing = await _context.Creators.FindAsync(id);
        if (existing == null) return NotFound();

        // Извлекаем данные из JWT токена текущего пользователя
        var currentLogin = User.FindFirst(JwtRegisteredClaimNames.Sub)?.Value; // Кто делает запрос
        var currentRole = User.FindFirst(ClaimTypes.Role)?.Value;            // Какая у него роль

        // ЛОГИКА ДОСТУПА: 
        // ADMIN может менять кого угодно. 
        // CUSTOMER может менять только свой профиль (сравнение текущего логина с логином в БД).
        if (currentRole != "ADMIN" && existing.Login != currentLogin)
            return StatusCode(403, new ErrorResponse { errorMessage = "Access denied", errorCode = "40301" });

        existing.Firstname = creator.Firstname;
        existing.Lastname = creator.Lastname;
        
        // Если при обновлении прислали новый пароль — заново его хэшируем
        if (!string.IsNullOrEmpty(creator.Password) && creator.Password != existing.Password) 
            existing.Password = BCrypt.Net.BCrypt.HashPassword(creator.Password, 4);
            
        await _context.SaveChangesAsync();
        return Ok(existing);
    }

    // DELETE: Удаление автора. Требует токен.
    [HttpDelete("{id}")]
    [Authorize]
    public async Task<IActionResult> Delete(int id)
    {
        var existing = await _context.Creators.FindAsync(id);
        if (existing == null) return NotFound();

        // Проверка личности через токен
        var currentLogin = User.FindFirst(JwtRegisteredClaimNames.Sub)?.Value;
        var currentRole = User.FindFirst(ClaimTypes.Role)?.Value;

        // Удалять может либо ADMIN, либо сам владелец аккаунта
        if (currentRole != "ADMIN" && existing.Login != currentLogin)
            return StatusCode(403, new ErrorResponse { errorMessage = "Access denied", errorCode = "40301" });

        _context.Creators.Remove(existing);
        await _context.SaveChangesAsync();
        
        // 204 No Content — стандартный ответ при успешном удалении
        return NoContent();
    }
}