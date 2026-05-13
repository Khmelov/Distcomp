using Microsoft.AspNetCore.Mvc;
using BusinessLogic.Servicies;
using BusinessLogic.DTO.Response;
using BusinessLogic.DTO.Request;
using DataAccess.Models;
using Microsoft.AspNetCore.Authorization;

namespace Presentation.Controllers
{
    // ==========================================
    // ВЕРСИЯ 1.0
    // ==========================================
    [Route("api/v1.0/marks")]
    [ApiController]
    public class MarksControllerV1 : BaseController<Mark, MarkRequestTo, MarkResponseTo>
    {
        public MarksControllerV1(IBaseService<MarkRequestTo, MarkResponseTo> service) : base(service)
        {
        }
    }

    // ==========================================
    // ВЕРСИЯ 2.0
    // ==========================================
    [Route("api/v2.0/marks")]
    [ApiController]
    [Authorize]
    public class MarksControllerV2 : BaseController<Mark, MarkRequestTo, MarkResponseTo>
    {
        public MarksControllerV2(IBaseService<MarkRequestTo, MarkResponseTo> service) : base(service)
        {
        }

        [HttpPost]
        public override async Task<ActionResult<MarkResponseTo>> CreateAsync([FromBody] MarkRequestTo entity)
        {
            if (!User.IsInRole("ADMIN")) return StatusCode(403, new { error = "Access denied. Only ADMIN can create marks." });
            return await base.CreateAsync(entity);
        }

        [HttpPut]
        public override async Task<ActionResult<MarkResponseTo>> UpdateAsync([FromBody] MarkRequestTo entity)
        {
            if (!User.IsInRole("ADMIN")) return StatusCode(403, new { error = "Access denied. Only ADMIN can update marks." });
            return await base.UpdateAsync(entity);
        }

        [HttpDelete("{id}")]
        public override async Task<ActionResult> Delete(int id)
        {
            if (!User.IsInRole("ADMIN")) return StatusCode(403, new { error = "Access denied. Only ADMIN can delete marks." });
            return await base.Delete(id);
        }
    }
}