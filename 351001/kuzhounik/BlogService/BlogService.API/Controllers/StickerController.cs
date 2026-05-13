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
[Route("api/v1.0/stickers")]
public class StickerControllerV1 : BaseController<long, StickerRequestToDto<long>, StickerResponseToDto<long>>
{
    public StickerControllerV1(IStickerService<long> stickerService) : base(stickerService) { }
}

// ==========================================
// ВЕРСИЯ 2.0 (С защитой JWT и ролями)
// ==========================================
[ApiController]
[Route("api/v2.0/stickers")]
[Authorize]
public class StickerControllerV2 : BaseController<long, StickerRequestToDto<long>, StickerResponseToDto<long>>
{
    public StickerControllerV2(IStickerService<long> stickerService) : base(stickerService) { }
    
    [HttpPost]
    public override async Task<ActionResult<StickerResponseToDto<long>>> Create([FromBody] StickerRequestToDto<long> entity)
    {
        if (!User.IsInRole("ADMIN")) return StatusCode(403, new { error = "Access denied. Only ADMIN can create marks." });
        return await base.Create(entity);
    }

    [HttpPut]
    public override async Task<IActionResult> Update([FromBody] StickerRequestToDto<long> entity)
    {
        if (!User.IsInRole("ADMIN")) return StatusCode(403, new { error = "Access denied. Only ADMIN can update marks." });
        return await base.Update(entity);
    }

    [HttpDelete("{id}")]
    public override async Task<IActionResult> Delete(long id)
    {
        if (!User.IsInRole("ADMIN")) return StatusCode(403, new { error = "Access denied. Only ADMIN can delete marks." });
        return await base.Delete(id);
    }
}