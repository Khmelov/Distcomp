using System.Security.Claims;
using BlogService.Application.DTOs.Request;
using BlogService.Application.DTOs.Response;
using BlogService.Application.Interfaces.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Shared.Controllers;

namespace BlogService.API.Controllers;

// ==========================================
// ВЕРСИЯ 1.0 (Без защиты)
// ==========================================
[ApiController]
[Route("api/v1.0/users")]
public class UserControllerV1 : BaseController<long, UserRequestToDto<long>, UserResponseToDto<long>>
{
    public UserControllerV1(IUserService<long> userService) : base(userService) {}
}

// ==========================================
// ВЕРСИЯ 2.0 (С защитой JWT и ролями)
// ==========================================
[Route("api/v2.0/users")]
[Authorize] // Защита для всего контроллера V2
public class CreatorsControllerV2 : BaseController<long, UserRequestToDto<long>, UserResponseToDto<long>>
{
    public CreatorsControllerV2(IUserService<long> userService) : base(userService) {}

    // Регистрация: Разрешаем доступ без токена
    [HttpPost]
    [AllowAnonymous]
    public override Task<ActionResult<UserResponseToDto<long>>> Create([FromBody] UserRequestToDto<long> entity)
    {
        return base.Create(entity);
    }

    // ОБНОВЛЕНИЕ: Только АДМИН или ВЛАДЕЛЕЦ ПРОФИЛЯ
    [HttpPut]
    public override async Task<IActionResult> Update([FromBody] UserRequestToDto<long> entity)
    {
        var currentUserLogin = User.FindFirstValue(ClaimTypes.NameIdentifier); // Получаем login (sub) из токена

        if (!User.IsInRole("ADMIN") && entity.Login != currentUserLogin)
        {
            return StatusCode(403, new { error = "Access denied. You can only update your own profile." });
        }

        return await base.Update(entity);
    }

    // УДАЛЕНИЕ: Только АДМИН или ВЛАДЕЛЕЦ ПРОФИЛЯ
    [HttpDelete("{id}")]
    public override async Task<IActionResult> Delete(long id)
    {
        if (!User.IsInRole("ADMIN"))
        {
            var targetCreator = await _service.GetAsync(id);
            var currentUserLogin = User.FindFirstValue(ClaimTypes.NameIdentifier);

            if (targetCreator == null || targetCreator.Login != currentUserLogin)
            {
                return StatusCode(403, new { error = "Access denied. You can only delete your own profile." });
            }
        }

        return await base.Delete(id);
    }
}