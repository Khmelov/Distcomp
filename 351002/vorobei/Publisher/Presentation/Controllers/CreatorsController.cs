using Microsoft.AspNetCore.Mvc;
using BusinessLogic.Servicies;
using BusinessLogic.DTO.Response;
using BusinessLogic.DTO.Request;
using DataAccess.Models;
using Microsoft.AspNetCore.Authorization;
using System.Security.Claims;

namespace Presentation.Controllers
{
    // ==========================================
    // ВЕРСИЯ 1.0 (Без защиты)
    // ==========================================
    [Route("api/v1.0/creators")]
    public class CreatorsControllerV1 : BaseController<Creator, CreatorRequestTo, CreatorResponseTo>
    {
        public CreatorsControllerV1(IBaseService<CreatorRequestTo, CreatorResponseTo> service) : base(service)
        {
        }
    }

    // ==========================================
    // ВЕРСИЯ 2.0 (С защитой JWT и ролями)
    // ==========================================
    [Route("api/v2.0/creators")]
    [Authorize] // Защита для всего контроллера V2
    public class CreatorsControllerV2 : BaseController<Creator, CreatorRequestTo, CreatorResponseTo>
    {
        public CreatorsControllerV2(IBaseService<CreatorRequestTo, CreatorResponseTo> service) : base(service)
        {
        }

        // РЕГИСТРАЦИЯ: Разрешаем доступ без токена
        [HttpPost]
        [AllowAnonymous]
        public override Task<ActionResult<CreatorResponseTo>> CreateAsync([FromBody] CreatorRequestTo entity)
        {
            return base.CreateAsync(entity);
        }

        // ОБНОВЛЕНИЕ: Только АДМИН или ВЛАДЕЛЕЦ ПРОФИЛЯ
        [HttpPut]
        public override async Task<ActionResult<CreatorResponseTo>> UpdateAsync([FromBody] CreatorRequestTo entity)
        {
            var currentUserLogin = User.FindFirstValue(ClaimTypes.NameIdentifier); // Получаем login (sub) из токена

            if (!User.IsInRole("ADMIN") && entity.Login != currentUserLogin)
            {
                return StatusCode(403, new { error = "Access denied. You can only update your own profile." });
            }

            return await base.UpdateAsync(entity);
        }

        // УДАЛЕНИЕ: Только АДМИН или ВЛАДЕЛЕЦ ПРОФИЛЯ
        [HttpDelete("{id}")]
        public override async Task<ActionResult> Delete(int id)
        {
            if (!User.IsInRole("ADMIN"))
            {
                var targetCreator = await _service.GetByIdAsync(id);
                var currentUserLogin = User.FindFirstValue(ClaimTypes.NameIdentifier);

                if (targetCreator == null || targetCreator.Login != currentUserLogin)
                {
                    return StatusCode(403, new { error = "Access denied. You can only delete your own profile." });
                }
            }

            return await base.Delete(id);
        }
    }
}